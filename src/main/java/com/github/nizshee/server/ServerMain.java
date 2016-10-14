package com.github.nizshee.server;


import com.github.nizshee.shared.GetMethod;
import com.github.nizshee.shared.ListMethod;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class ServerMain {

    private final static Map<Integer, ServerMethodWrapper> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put(1, new ServerMethodWrapper(new ListMethod()));
        handlers.put(2, new ServerMethodWrapper(new GetMethod()));
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(InetAddress.getByName("localhost"), 8080);
        server.startSync(handlers);
//        server.start(handlers);
//        Thread.sleep(500);
//        Socket socket = new Socket("localhost", 8080);
//        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//        DataInputStream dis = new DataInputStream(socket.getInputStream());
//        dos.writeInt(1);
//        dos.writeUTF("/home/roman");
//        Thread.sleep(500);
//        int result = dis.readInt();
//        System.out.println(result + 1);
//        server.stop();
    }
}
