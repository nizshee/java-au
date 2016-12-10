package com.github.nizshee.client.util;


import com.github.nizshee.client.ClientKeeperImpl;
import com.github.nizshee.client.procedure.ClientRegistry;
import com.github.nizshee.shared.exception.WrongDataException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


@SuppressWarnings("all")
public class Peer {
    private final ConnectionWrapper connection;
    private final Map<Integer, Set<Integer>> parts = new HashMap<>();
    private final ClientKeeperImpl client;

    public Peer(ClientKeeperImpl client, ConnectionWrapper connection) {
        this.connection = connection;
        this.client = client;
    }

    public void resolve(int identifier, Set<Integer> toDownload, Function<FilePart, Void> onComplete) {
        if (!parts.containsKey(identifier)) {
            parts.put(identifier, new HashSet<>());
            try {
                connection.add(ClientRegistry.STATW, identifier, ps -> {
                    parts.put(identifier, ps);
                    ps.retainAll(toDownload);
                    for (int part: ps) {
                        FilePart fp = new FilePart(identifier, part);
                        try {
                            connection.add(ClientRegistry.GETW, fp, bytes -> {
                                onComplete.apply(fp);
                                client.put(fp, bytes);
                                return null;
                            });
                        } catch (WrongDataException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                });
            } catch (WrongDataException ignore) {
            }
        }
    }

    public void runNow() throws IOException {
        connection.runNow();
    }

    public void disconnect() {
        try {
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
