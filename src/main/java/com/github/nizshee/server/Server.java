package com.github.nizshee.server;


import com.github.nizshee.shared.ServerNotRunningException;
import com.github.nizshee.shared.ServerRunningException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Map;

public class Server {

    private final InetAddress host;
    private final Integer port;
    private ServerSocket socket = null;

    public Server(InetAddress host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void start(Map<Integer, Handler> handlers) throws IOException, ServerRunningException {
        if (socket != null) throw new ServerRunningException();
        socket = new ServerSocket(port, 50, host);
        Thread thread = new Thread(new ServerRunnable(socket, handlers));
        thread.start();
    }

    @SuppressWarnings("all")
    public void startSync(Map<Integer, Handler> handlers)
            throws IOException, ServerRunningException {
        synchronized (this) {
            if (socket != null) throw new ServerRunningException();
            socket = new ServerSocket(port, 50, host);
        }
        (new ServerRunnable(socket, handlers)).run();
    }

    public synchronized void stop() throws IOException, ServerNotRunningException {
        if (socket == null) throw new ServerNotRunningException();
        socket.close();
        socket = null;
    }

    private static class ServerRunnable implements Runnable {

        private final ServerSocket serverSocket;
        private final Map<Integer, Handler> handlers;

        ServerRunnable(ServerSocket serverSocket, Map<Integer, Handler> handlers) {
            this.serverSocket = serverSocket;
            this.handlers = handlers;
        }

        public void run() {
            try {
                System.out.println("Server started.");
                //noinspection InfiniteLoopStatement
                while (true) {
                    Socket socket = serverSocket.accept();
                    try {
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        Integer key = dis.readInt();
                        if (handlers.containsKey(key)) {
                            Handler wrapper = handlers.get(key);
                            wrapper.handle(dis, dos);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        socket.close();
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
