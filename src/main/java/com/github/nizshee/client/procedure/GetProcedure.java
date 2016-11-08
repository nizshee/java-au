package com.github.nizshee.client.procedure;


import com.github.nizshee.client.ClientKeeper;
import com.github.nizshee.client.util.FilePart;
import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.exception.WrongDataException;
import com.github.nizshee.shared.procedure.RemoteProcedure;

import java.io.*;

@SuppressWarnings("all")
public class GetProcedure extends RemoteProcedure<ClientKeeper, FilePart, byte[]> {

    private final Dumper<FilePart> request = new Dumper<FilePart>() {
        @Override
        public byte[] dump(FilePart filePart) throws WrongDataException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(filePart.identifier);
                dos.writeInt(filePart.part);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't write get.");
            }
        }

        @Override
        public FilePart restore(byte[] bytes) throws WrongDataException {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bais);
                int identifier = dis.readInt();
                int part = dis.readInt();
                return new FilePart(identifier, part);
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't read get.");
            }
        }
    };

    private final Dumper<byte[]> response = new Dumper<byte[]>() {
        @Override
        public byte[] dump(byte[] bytes) throws WrongDataException {
            return bytes;
        }

        @Override
        public byte[] restore(byte[] bytes) throws WrongDataException {
            return bytes;
        }
    };

    @Override
    protected Dumper<FilePart> request() {
        return request;
    }

    @Override
    protected Dumper<byte[]> response() {
        return response;
    }

    @Override
    protected byte[] execute(byte[] ip, ClientKeeper clientKeeper, FilePart filePart) {
        return clientKeeper.get(filePart);
    }
}
