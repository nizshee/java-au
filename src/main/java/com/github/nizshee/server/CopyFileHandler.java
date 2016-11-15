package com.github.nizshee.server;


import java.io.*;


@SuppressWarnings("all")
public class CopyFileHandler implements Handler {
    @Override
    public void handle(DataInputStream dis, DataOutputStream dos) throws IOException {
        String name = dis.readUTF();
        File file = new File(name);
        try (FileInputStream fis = new FileInputStream(file)) {
            if (!file.exists()) {
                dos.writeLong(0);
                return;
            }
            dos.writeLong(file.length());
            byte[] buffer = new byte[1024];
            int size = 0;
            while (size != -1) {
                dos.write(buffer, 0, size);
                size = fis.read(buffer);
            }
        }
    }
}
