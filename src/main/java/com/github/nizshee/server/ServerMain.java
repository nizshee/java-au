package com.github.nizshee.server;


import com.github.nizshee.shared.ListMethod;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class ServerMain {

    private final static Map<Integer, Handler> HANDLERS;

    static {
        HANDLERS = new HashMap<>();
        HANDLERS.put(1, new MethodWrapper(new ListMethod()));
        HANDLERS.put(2, new CopyFileHandler());
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(InetAddress.getByName("localhost"), 8080);
        server.startSync(HANDLERS);
    }
}
