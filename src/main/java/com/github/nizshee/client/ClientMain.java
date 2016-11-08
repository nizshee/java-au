package com.github.nizshee.client;


import com.github.nizshee.client.procedure.ClientRegistry;
import com.github.nizshee.client.util.ConnectionWrapper;
import com.github.nizshee.client.util.Downloader;
import com.github.nizshee.server.procedure.ServerRegistry;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.util.UpdateItem;
import com.github.nizshee.server.util.UploadItem;
import com.github.nizshee.shared.Client;
import com.github.nizshee.shared.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ClientMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        short port = Short.parseShort(args[0]);

        ClientKeeperImpl clientKeeper = restoreKeeper();
        ClientRegistry clientRegistry = new ClientRegistry(clientKeeper);
        Server clientServer = new Server(clientRegistry);
        Client serverConnection1 = new Client();

        Downloader downloader = new Downloader();
        clientServer.start(new InetSocketAddress("localhost", port));
        serverConnection1.connect(new InetSocketAddress("localhost", 8081));

        ConnectionWrapper serverConnection = new ConnectionWrapper(serverConnection1);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        LocalDateTime updateTime = LocalDateTime.now();

        label:
        while (true) {
            if (in.ready()) {
                String input = in.readLine();
                try {
                    String[] parts = input.split(" ");
                    switch (parts[0]) {
                        case "exit":
                            break label;
                        case "list": {
                            serverConnection.add(ServerRegistry.LISTW, new Object(), list -> {
                                downloader.updateList(list);
                                System.out.println("list:");
                                list.forEach(System.out::println);
                                return null;
                            });
                            break;
                        }
                        case "upload": {
                            String name = parts[1];
                            if (!new File(name).exists()) throw new Exception("File not exists.");
                            long size = (new File(name)).length();
                            serverConnection.add(ServerRegistry.UPLOADW, new UploadItem(name, size), identifier -> {
                                clientKeeper.upload(new FileDescriptor(identifier, name, size));
                                System.out.println("file added with id " + identifier);
                                update(clientKeeper, serverConnection, port);
                                return null;
                            });
                            break;
                        }
                        case "get": {
                            int identifier = Integer.parseInt(parts[1]);
                            Optional<FileDescriptor> optional = downloader.resolve(identifier);
                            if (!optional.isPresent()) {
                                System.out.println("Maybe you should get list first?");
                            } else {
                                clientKeeper.get(optional.get());
                                downloader.clear();
                            }
                            break;
                        }
                        case "sources": {
                            int identifier = Integer.parseInt(parts[1]);
                            serverConnection.add(ServerRegistry.SOURCESW, identifier, sources -> {
                                System.out.println("sources:");
                                sources.forEach(System.out::println);
                                return null;
                            });
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (updateTime.isBefore(LocalDateTime.now())) {
                downloader.clear();
                update(clientKeeper, serverConnection, port);
                updateTime = LocalDateTime.now().plusMinutes(4);
            }

            downloader.runNow(clientKeeper, serverConnection);
            serverConnection.runNow();
            clientServer.runNow();
        }

        downloader.clear();
        clientServer.stop();
        serverConnection.disconnect();

        dumpKeeper(clientKeeper);
    }

    static private void update(ClientKeeperImpl client, ConnectionWrapper server, short port) {
        try {
            List<Integer> ids = client.update();
            server.add(ServerRegistry.UPDATEW, new UpdateItem(port, ids), result -> {
                if (!result) update(client, server, port);
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ClientKeeperImpl restoreKeeper() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(".client-keeper")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (ClientKeeperImpl) ois.readObject();
        } catch (FileNotFoundException ignore) {
            return new ClientKeeperImpl();
        }
    }

    private static void dumpKeeper(ClientKeeperImpl keeper) throws IOException {
        FileOutputStream fos = new FileOutputStream(".client-keeper");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(keeper);
        fos.close();
    }

}
