package com.github.nizshee.server;


import com.github.nizshee.shared.Method;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MethodWrapper<Request, Response> implements Handler {
    private final Method<Request, Response> method;

    public MethodWrapper(Method<Request, Response> method) {
        this.method = method;
    }

    @Override
    public void handle(DataInputStream dis, DataOutputStream dos) throws IOException {
        Request request = method.readRequest(dis);
        Response response = method.execute(request);
        method.writeResponse(dos, response);
    }
}
