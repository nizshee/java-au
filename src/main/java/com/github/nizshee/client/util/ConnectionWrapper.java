package com.github.nizshee.client.util;


import com.github.nizshee.shared.Client;
import com.github.nizshee.shared.exception.WrongDataException;
import com.github.nizshee.shared.procedure.ProcedureWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ConnectionWrapper {

    private final Client client;

    private final LinkedList<byte[]> data;
    private final LinkedList<Function<byte[], Void>> callbacks;
    private final boolean isWaiting;

    public ConnectionWrapper(Client client) {
        this.client = client;
        data = new LinkedList<>();
        callbacks = new LinkedList<>();
        isWaiting = false;
    }

    public <Request, Response> void add(ProcedureWrapper<Request, Response> procedure, Request request,
                                        Function<Response, Void> callback)
            throws WrongDataException {
        byte[] bytesRequest = procedure.dump(request);
        data.add(bytesRequest);
        callbacks.add(bytesResponse -> {
            try {
                Response response = procedure.restore(bytesResponse);
                callback.apply(response);
            } catch (WrongDataException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void runNow() throws IOException {
        if (!isWaiting && !data.isEmpty()) {
            client.write(data.pollFirst());
        }
        List<byte[]> d = client.runNow();
        if (!d.isEmpty()) {
            Function<byte[], Void> callback = callbacks.pollFirst();
            callback.apply(d.get(0));
        }
    }

    public void disconnect() throws IOException {
        client.disconnect();
    }
}
