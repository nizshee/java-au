package com.github.nizshee.client;


import com.github.nizshee.client.procedure.ClientRegistry;
import com.github.nizshee.client.util.ConnectionWrapper;
import com.github.nizshee.client.util.Downloader;
import com.github.nizshee.client.util.FilePart;
import com.github.nizshee.server.procedure.ServerRegistry;
import com.github.nizshee.server.util.*;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.shared.Client;
import com.github.nizshee.shared.Server;
import com.github.nizshee.shared.exception.WrongDataException;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("all")
public class TorrentClient {

    private final short port;

    private final ClientKeeperImpl clientKeeper;
    private final Server clientServer;
    private final Client serverConnection1 = new Client();

    private final Downloader downloader;

    private final ConnectionWrapper serverConnection = new ConnectionWrapper(serverConnection1);

    private LocalDateTime updateTime;

    public TorrentClient(short port, Function<FilePart, Void> onComplete) throws ClassNotFoundException, IOException {
        this.port = port;

        clientKeeper = restoreKeeper();
        ClientRegistry clientRegistry = new ClientRegistry(clientKeeper);
        clientServer = new Server(clientRegistry);
        downloader = new Downloader(onComplete);

        clientServer.start(new InetSocketAddress("localhost", port));
        serverConnection1.connect(new InetSocketAddress("localhost", 8081));
        updateTime = LocalDateTime.now();
    }

    public synchronized void close() throws IOException {
        downloader.clear();
        clientServer.stop();
        serverConnection.disconnect();

        dumpKeeper(clientKeeper);
    }

    public synchronized void runNow() throws IOException {
        if (updateTime.isBefore(LocalDateTime.now())) {
            downloader.clear();
            update(clientKeeper, serverConnection, port);
            updateTime = LocalDateTime.now().plusMinutes(4);
        }

        downloader.runNow(clientKeeper, serverConnection);
        serverConnection.runNow();
        clientServer.runNow();
    }

    public synchronized void list(Function<List<FileDescriptor>, Void> func) throws WrongDataException {
        serverConnection.add(ServerRegistry.LISTW, new Object(), list -> {
            downloader.updateList(list);
            func.apply(list);
            return null;
        });
    }

    public synchronized void upload(String name, Function<Integer, Void> func) throws Exception {
        if (!new File(name).exists()) throw new Exception("File not exists.");
        long size = (new File(name)).length();
        serverConnection.add(ServerRegistry.UPLOADW, new UploadItem(name, size), identifier -> {
            clientKeeper.upload(new FileDescriptor(identifier, name, size));
            update(clientKeeper, serverConnection, port);
            func.apply(identifier);
            return null;
        });
    }

    public synchronized void get(int identifier) {
        Optional<FileDescriptor> optional = downloader.resolve(identifier);
        if (!optional.isPresent()) {
            System.out.println("Maybe you should get list first?");
        } else {
            clientKeeper.get(optional.get());
            downloader.clear();
        }
    }

    public synchronized int info(int identifier) {
        Map<Integer, Set<Integer>> m = clientKeeper.getToDownload();
        if(!m.containsKey(identifier)) return -1;
        return m.get(identifier).size();
    }

    public synchronized void sources(int identifier, Function<List<ClientItem>, Void> func) throws WrongDataException {
        serverConnection.add(ServerRegistry.SOURCESW, identifier, sources -> {
            func.apply(sources);
            return null;
        });
    }

    private static void update(ClientKeeperImpl client, ConnectionWrapper server, short port) {
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
