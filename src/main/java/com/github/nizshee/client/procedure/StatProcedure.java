package com.github.nizshee.client.procedure;


import com.github.nizshee.client.ClientKeeper;
import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.exception.WrongDataException;
import com.github.nizshee.shared.procedure.RemoteProcedure;
import com.github.nizshee.shared.util.IntDumper;

import java.io.*;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
public class StatProcedure extends RemoteProcedure<ClientKeeper, Integer, Set<Integer>> {

    private final Dumper<Integer> request = new IntDumper();

    private final Dumper<Set<Integer>> response = new Dumper<Set<Integer>>() {
        @Override
        public byte[] dump(Set<Integer> identifiers) throws WrongDataException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(identifiers.size());
                for (int identifier: identifiers) {
                    dos.writeInt(identifier);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't write stat.");
            }
        }

        @Override
        public Set<Integer> restore(byte[] bytes) throws WrongDataException {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bais);

                int count = dis.readInt();
                Set<Integer> identifiers = new HashSet<>(count);
                for (int i = 0; i < count; ++i) {
                    int identifier = dis.readInt();
                    identifiers.add(identifier);
                }
                return identifiers;
            } catch (IOException e) {
                e.printStackTrace();
                throw new WrongDataException("Can't read stat.");
            }
        }
    };


    @Override
    protected Dumper<Integer> request() {
        return request;
    }

    @Override
    protected Dumper<Set<Integer>> response() {
        return response;
    }

    @Override
    protected Set<Integer> execute(byte[] ip, ClientKeeper clientKeeper, Integer integer) {
        return clientKeeper.stat(integer);
    }
}
