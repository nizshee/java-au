package com.github.nizshee.server;


import com.github.nizshee.server.util.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ServerKeeperImpl implements ServerKeeper, Serializable {

    private int identifierCounter = 0;
    private List<FileDescriptor> files = new LinkedList<>();
    private Map<ClientItem, LocalDateTime> clients = new HashMap<>();
    private Map<ClientItem, List<Integer>> clientIds = new HashMap<>();

    @Override
    public List<FileDescriptor> list() {
        return files;
    }

    @Override
    public int upload(UploadItem item) {
        files.add(new FileDescriptor(identifierCounter, item.name, item.size));
        return identifierCounter++;
    }

    @Override
    public List<ClientItem> sources(int identifier) {
        return clientIds.keySet().stream()
                .filter(id -> clientIds.get(id)
                .contains(identifier))
                .collect(Collectors.toList());

    }

    @Override
    public boolean update(byte[] ip, UpdateItem item) {
        ClientItem id = new ClientItem(ip[0], ip[1], ip[2], ip[3], item.port);
        clients.put(id, LocalDateTime.now().plusMinutes(5));
        clientIds.put(id, item.identifiers);
        return true;
    }

    public void clearOld(LocalDateTime current) {
        Set<ClientItem> set = clients.keySet().stream()
                .filter(id -> clients.get(id).isBefore(current))
                .collect(Collectors.toSet());

        set.forEach(id -> {
            clients.remove(id);
            clientIds.remove(id);
        });
    }
}
