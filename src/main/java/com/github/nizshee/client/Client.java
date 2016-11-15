package com.github.nizshee.client;


import com.github.nizshee.shared.ListMethod;
import com.github.nizshee.shared.Method;
import com.github.nizshee.shared.RemoteFile;

import java.io.*;
import java.net.Socket;
import java.util.List;

@SuppressWarnings("all")
public class Client {

    private final String host;
    private final int port;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    public List<RemoteFile> getList(String name) throws IOException {
        return Method.getValue(new Socket(host, port), 1, new ListMethod(), name);
    }

    public void getFile(String name) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(2);
            dos.writeUTF(name);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            long size = dis.readLong();
            byte[] buffer = new byte[1024];
            File file = new File(name);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                long total = 0;
                int partSize = 0;
                while (partSize != -1 && total < size) {
                    fos.write(buffer, 0, (int) Math.min(size - total, partSize));
                    total += partSize;
                    partSize = dis.read(buffer);
                }
                if (total != size) throw new IOException("Not enough bytes");
            }
        }
    }
}
