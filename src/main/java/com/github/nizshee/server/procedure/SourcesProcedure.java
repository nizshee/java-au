package com.github.nizshee.server.procedure;


import com.github.nizshee.server.ServerKeeper;
import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.shared.util.IntDumper;
import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.procedure.RemoteProcedure;
import com.github.nizshee.shared.exception.WrongDataException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class SourcesProcedure extends RemoteProcedure<ServerKeeper, Integer, List<ClientItem>> {

    private final Dumper<Integer> request = new IntDumper();

    private final Dumper<List<ClientItem>> response = new Dumper<List<ClientItem>>() {
        @Override
        public byte[] dump(List<ClientItem> items) throws WrongDataException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(items.size());
                for (ClientItem item: items) {
                    dos.writeByte(item.ip0);
                    dos.writeByte(item.ip1);
                    dos.writeByte(item.ip2);
                    dos.writeByte(item.ip3);
                    dos.writeShort(item.port);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't write sources.");
            }
        }

        @Override
        public List<ClientItem> restore(byte[] bytes) throws WrongDataException {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bais);

                int count = dis.readInt();
                List<ClientItem> list = new ArrayList<>(count);
                for (int i = 0; i < count; ++i) {
                    byte ip0 = dis.readByte();
                    byte ip1 = dis.readByte();
                    byte ip2 = dis.readByte();
                    byte ip3 = dis.readByte();
                    short port = dis.readShort();
                    list.add(i, new ClientItem(ip0, ip1, ip2, ip3, port));
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't read sources.");
            }
        }
    };

    @Override
    protected Dumper<Integer> request() {
        return request;
    }

    @Override
    protected Dumper<List<ClientItem>> response() {
        return response;
    }

    @Override
    protected List<ClientItem> execute(byte[] ip, ServerKeeper keeper, Integer integer) {
        return keeper.sources(integer);
    }
}
