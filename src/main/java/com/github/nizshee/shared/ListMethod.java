package com.github.nizshee.shared;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ListMethod implements Method<String, List<RemoteFile>> {

    @Override
    public List<RemoteFile> apply(String name) {
        Path dir = Paths.get(name);
        File[] files = dir.toFile().listFiles();
        if (files == null) return Collections.emptyList();
        List<RemoteFile> list = new LinkedList<>();
        for (File file: files) {
            list.add(new RemoteFile(file.getName(), file.isDirectory()));
        }
        return list;
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
    public void writeResult(DataOutputStream dos, List<RemoteFile> remoteFiles) throws IOException {
        dos.writeInt(remoteFiles.size());
        for (RemoteFile file: remoteFiles) {
            dos.writeUTF(file.name);
            dos.writeBoolean(file.isDirectory);
        }
    }

    @Override
    public List<RemoteFile> readResult(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<RemoteFile> list = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            String name = dis.readUTF();
            boolean isDirectory = dis.readBoolean();
            list.add(new RemoteFile(name, isDirectory));
        }
        return list;
    }
}
