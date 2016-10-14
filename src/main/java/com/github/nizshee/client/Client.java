package com.github.nizshee.client;


import com.github.nizshee.shared.GetMethod;
import com.github.nizshee.shared.ListMethod;
import com.github.nizshee.shared.Method;
import com.github.nizshee.shared.RemoteFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {

    private final String host;
    private final int port;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    List<RemoteFile> getList(String name) throws IOException {
        return getValue(new Socket(host, port), 1, new ListMethod(), name);
    }

    byte[] getFile(String name) throws IOException {
        return getValue(new Socket(host, port), 2, new GetMethod(), name);
    }

    public static <Value, Result> Result getValue(Socket socket, int code, Method<Value, Result> method, Value value)
            throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        dos.writeInt(code);
        method.writeValue(dos, value);
        return method.readResult(dis);
    }
}
