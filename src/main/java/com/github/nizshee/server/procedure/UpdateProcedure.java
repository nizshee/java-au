package com.github.nizshee.server.procedure;


import com.github.nizshee.server.ServerKeeper;
import com.github.nizshee.server.util.UpdateItem;
import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.procedure.RemoteProcedure;
import com.github.nizshee.shared.exception.WrongDataException;
import com.github.nizshee.shared.util.BooleanDumper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class UpdateProcedure extends RemoteProcedure<ServerKeeper, UpdateItem, Boolean> {

    private final Dumper<UpdateItem> request = new Dumper<UpdateItem>() {
        @Override
        public byte[] dump(UpdateItem item) throws WrongDataException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeShort(item.port);
                dos.writeInt(item.identifiers.size());
                for (int identifier: item.identifiers) {
                    dos.writeInt(identifier);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't write list.");
            }
        }

        @Override
        public UpdateItem restore(byte[] bytes) throws WrongDataException {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bais);

                short port = dis.readShort();
                int count = dis.readInt();
                List<Integer> identifiers = new ArrayList<>(count);
                for (int i = 0; i < count; ++i) {
                    int identifier = dis.readInt();
                    identifiers.add(i, identifier);
                }
                return new UpdateItem(port, identifiers);
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't read list.");
            }
        }
    };

    private final Dumper<Boolean> response = new BooleanDumper();

    @Override
    protected Dumper<UpdateItem> request() {
        return request;
    }

    @Override
    protected Dumper<Boolean> response() {
        return response;
    }

    @Override
    protected Boolean execute(byte[] ip, ServerKeeper keeper, UpdateItem updateItem) {
        return keeper.update(ip, updateItem);
    }
}
