package com.github.nizshee.client;


import com.github.nizshee.server.util.FileDescriptor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;


public class ClientGui extends Application {
    private static TorrentClient torrentClient;
    private static Thread serverThread;
    private static ListView<FileDescriptor> listView;


    public static void main(String[] args) throws Exception {
        short port = Short.parseShort(args[0]);
        torrentClient = new TorrentClient(port, fp -> {
            if (listView != null) listView.refresh();
            return null;
        });
        serverThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    torrentClient.runNow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        listView = new ListView<>();

        ObservableList<FileDescriptor> list = FXCollections.observableArrayList();
        listView.setCellFactory(param -> new FileCell());
        listView.setItems(list);

        primaryStage.setTitle("Torrent Client");
        Button btn = new Button();
        btn.setText("Refresh list");
        btn.setOnAction(event -> updateList());

        Button btnInput = new Button();
        btnInput.setText("Add file");
        btnInput.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Upload file");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter file name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                try {
                    torrentClient.upload(name, identifier -> {
                        Platform.runLater(ClientGui::updateList);
                        return null;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        StackPane root = new StackPane();
        VBox vbox = new VBox();
        vbox.getChildren().addAll(btn, btnInput, listView);
        root.getChildren().add(vbox);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        torrentClient.close();
        serverThread.interrupt();
        serverThread.join();
    }

    private static void updateList() {
        try {
            torrentClient.list(files -> {
                Platform.runLater(() -> listView.setItems(FXCollections.observableList(files)));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class FileCell extends ListCell<FileDescriptor> {
        private final HBox hbox;
        private final Text name;
        private final Text progress;
        private final Button btn;

        FileCell() {
            hbox = new HBox();
            name = new Text();
            progress = new Text();
            btn = new Button("download");
            Pane pane = new Pane();
            hbox.getChildren().addAll(name, pane, progress, btn);
            HBox.setHgrow(pane, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(FileDescriptor item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                int info = torrentClient.info(item.id);
                boolean isDownloading = info != -1;
                int p = (int) (1 - ((double) info / (item.size / (ClientKeeperImpl.PART_SIZE)))) * 100;
                name.setText(item.name + " (" + item.size + "b)");
                progress.setText("" +  Math.max(0, Math.min(100, p)));
                progress.setManaged(isDownloading);
                progress.setVisible(isDownloading);
                btn.setManaged(!isDownloading);
                btn.setVisible(!isDownloading);
                btn.setOnAction(event -> {
                    torrentClient.get(item.id);
                    listView.refresh();
                });
                setGraphic(hbox);
            }
        }
    }
}