package com.github.nizshee.client.util;


import com.github.nizshee.client.ClientKeeperImpl;
import com.github.nizshee.server.procedure.ServerRegistry;
import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.shared.Client;
import com.github.nizshee.shared.exception.WrongDataException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Function;


/**
 * Naive downloader.
 */
public class Downloader {

    private List<FileDescriptor> list = new LinkedList<>();
    private boolean isClear = true;
    private Map<ClientItem, Peer> peers = new HashMap<>();
    private final Function<FilePart, Void> onComplete;

    public Downloader(Function<FilePart, Void> onComplete) {
        this.onComplete = onComplete;
    }


    public void runNow(ClientKeeperImpl client, ConnectionWrapper server) {
        if (isClear) {
            prepare(client, server);
            isClear = false;
        } else if (client.getToDownload().isEmpty()) {
            clear();
        } else {
            for (Iterator<Map.Entry<ClientItem, Peer>> it = peers.entrySet().iterator(); it.hasNext();) {
                Map.Entry<ClientItem, Peer> entry = it.next();
                try {
                    entry.getValue().runNow();
                } catch (IOException e) {
                    e.printStackTrace();
                    it.remove();
                }
            }
        }

    }

    public void clear() {
        peers.values().forEach(Peer::disconnect);
        peers.clear();
        isClear = true;
    }

    @SuppressWarnings("all")
    private void prepare(ClientKeeperImpl client, ConnectionWrapper server) {
        for (int id: client.getToDownload().keySet()) {
            try {
                server.add(ServerRegistry.SOURCESW, id, clientItems -> {
                    for (ClientItem item: clientItems) {
                        if (!peers.containsKey(item)) {
                            Client client1 = new Client();
                            byte[] ip = {item.ip0, item.ip1, item.ip2, item.ip3};
                            try {
                                client1.connect(new InetSocketAddress(InetAddress.getByAddress(ip), item.port));
                                ConnectionWrapper wrapper = new ConnectionWrapper(client1);
                                Peer peer = new Peer(client, wrapper);
                                peer.resolve(id, client.getToDownload().get(id), onComplete);
                                peers.put(item, peer);
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    client1.disconnect();
                                } catch (IOException ignore) {}
                            }
                        }
                    }
                    return null;
                });
            } catch (WrongDataException ignore) {
            }
        }
    }

    public void updateList(List<FileDescriptor> list) {
        this.list = list;
    }

    public Optional<FileDescriptor> resolve(int identifier) {
        return list.stream().filter(i -> i.id == identifier).findAny();
    }

}
