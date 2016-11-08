package com.github.nizshee.server.procedure;


import com.github.nizshee.server.ServerKeeper;
import com.github.nizshee.shared.util.IntDumper;
import com.github.nizshee.server.util.UploadItem;
import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.procedure.RemoteProcedure;
import com.github.nizshee.shared.exception.WrongDataException;

import java.io.*;

@SuppressWarnings("all")
public class UploadProcedure extends RemoteProcedure<ServerKeeper, UploadItem, Integer>{
    private final Dumper<UploadItem> request = new Dumper<UploadItem>() {
        @Override
        public byte[] dump(UploadItem item) throws WrongDataException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeUTF(item.name);
                dos.writeLong(item.size);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't write upload.");
            }
        }

        @Override
        public UploadItem restore(byte[] bytes) throws WrongDataException {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bais);
                String name = dis.readUTF();
                long size = dis.readLong();
                return new UploadItem(name, size);
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't read list.");
            }
        }
    };

    private final Dumper<Integer> response = new IntDumper();

    @Override
    public Dumper<UploadItem> request() {
        return request;
    }

    @Override
    public Dumper<Integer> response() {
        return response;
    }

    @Override
    protected Integer execute(byte[] ip, ServerKeeper keeper, UploadItem uploadItem) {
        return keeper.upload(uploadItem);
    }
}
