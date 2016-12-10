package com.github.nizshee.server;


import com.github.nizshee.server.procedure.ServerRegistry;
import com.github.nizshee.shared.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class ServerMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ServerKeeperImpl serverKeeper = restoreKeeper();
        ServerRegistry serverRegistry = new ServerRegistry(serverKeeper);
        Server server = new Server(serverRegistry);

        server.start(new InetSocketAddress("localhost", 8081));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (in.ready()) {
                String input = in.readLine();
                if (input.equals("exit")) {
                    break;
                } else {
                    System.out.println("input 'exit'");
                }
            }
            server.runNow();
            serverKeeper.clearOld(LocalDateTime.now());
        }

        dumpKeeper(serverKeeper);
    }

    private static ServerKeeperImpl restoreKeeper() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(".server-keeper")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (ServerKeeperImpl) ois.readObject();
        } catch (FileNotFoundException ignore) {
            return new ServerKeeperImpl();
        }
    }

    private static void dumpKeeper(ServerKeeperImpl keeper) throws IOException {
        FileOutputStream fos = new FileOutputStream(".server-keeper");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(keeper);
        fos.close();
    }
}
