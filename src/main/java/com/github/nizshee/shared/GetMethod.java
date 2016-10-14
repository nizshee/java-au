package com.github.nizshee.shared;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetMethod implements Method<String, byte[]> {

    @Override
    public byte[] apply(String s) {
        try {
            return Files.readAllBytes(Paths.get(s));
        } catch (IOException ignore) {
            return new byte[0];
        }
    }

    @Override
    public void writeValue(DataOutputStream dos, String s) throws IOException {
        dos.writeUTF(s);
    }

    @Override
    public String readValue(DataInputStream dis) throws IOException {
        return dis.readUTF();
    }

    @Override
    public void writeResult(DataOutputStream dos, byte[] bytes) throws IOException {
        dos.writeInt(bytes.length);
        dos.write(bytes, 0, bytes.length);
        for (Byte aByte : bytes) {
            dos.writeByte(aByte);
        }
    }

    @Override
    public byte[] readResult(DataInputStream dis) throws IOException {
        int length = dis.readInt();
        byte[] bytes = new byte[length];
        int read = dis.read(bytes, 0, length);
        if (read != length) throw new IOException("Bad read,");
        return bytes;
    }
}
