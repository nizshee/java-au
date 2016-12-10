package com.github.nizshee.server.procedure;


import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.ServerKeeper;
import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.procedure.RemoteProcedure;
import com.github.nizshee.shared.exception.WrongDataException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ListProcedure extends RemoteProcedure<ServerKeeper, Object, List<FileDescriptor>> {

    private Dumper<Object> request = new Dumper<Object>() {
        @Override
        public byte[] dump(Object o) throws WrongDataException {
            return new byte[0];
        }

        @Override
        public Object restore(byte[] bytes) throws WrongDataException {
            return new Object();
        }
    };

    private Dumper<List<FileDescriptor>> response = new Dumper<List<FileDescriptor>>() {
        @Override
        public byte[] dump(List<FileDescriptor> items) throws WrongDataException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(items.size());
                for (FileDescriptor item: items) {
                    dos.writeInt(item.id);
                    dos.writeUTF(item.name);
                    dos.writeLong(item.size);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't write list.");
            }
        }

        @Override
        public List<FileDescriptor> restore(byte[] bytes) throws WrongDataException {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bais);

                int count = dis.readInt();
                List<FileDescriptor> list = new ArrayList<>(count);
                for (int i = 0; i < count; ++i) {
                    int id = dis.readInt();
                    String name = dis.readUTF();
                    long size = dis.readLong();
                    list.add(i, new FileDescriptor(id, name, size));
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't read list.");
            }
        }
    };

    @Override
    protected Dumper<Object> request() {
        return request;
    }

    @Override
    protected Dumper<List<FileDescriptor>> response() {
        return response;
    }

    @Override
    protected List<FileDescriptor> execute(byte[] ip, ServerKeeper keeper, Object o) {
        return keeper.list();
    }


}
