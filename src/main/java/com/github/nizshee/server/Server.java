package com.github.nizshee.server;


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

    public void start(Map<Integer, ServerMethodWrapper> handlers) throws IOException {
        if (socket != null) throw new IOException("Server already running.");
        socket = new ServerSocket(port, 50, host);
        Thread thread = new Thread(new ServerRunnable(socket, handlers));
        thread.start();
    }

    public void startSync(Map<Integer, ServerMethodWrapper> handlers) throws IOException {
        if (socket != null) throw new IOException("Server already running.");
        socket = new ServerSocket(port, 50, host);
        (new ServerRunnable(socket, handlers)).run();
    }

    public void stop() throws IOException {
        socket.close();
        socket = null;
    }

    private class ServerRunnable implements Runnable {

        private final ServerSocket serverSocket;
        private final Map<Integer, ServerMethodWrapper> handlers;

        ServerRunnable(ServerSocket serverSocket, Map<Integer, ServerMethodWrapper> handlers) {
            this.serverSocket = serverSocket;
            this.handlers = handlers;
        }

        public void run() {
            try {
                System.out.println("Server started.");
                //noinspection InfiniteLoopStatement
                while (true) {
                    Socket socket = serverSocket.accept();
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    Integer key = dis.readInt();
                    if (handlers.containsKey(key)) {
                        ServerMethodWrapper wrapper = handlers.get(key);
                        wrapper.handle(dis, dos);
                    }
                    socket.close();
                }
            } catch (SocketException ignore) {
                System.out.println("Server stopped.");
            } catch (IOException e) {
                System.err.println("Can't accept connection.");
            }
        }
    }
}
