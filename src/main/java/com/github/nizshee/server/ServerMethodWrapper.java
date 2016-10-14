package com.github.nizshee.server;


import com.github.nizshee.shared.Method;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerMethodWrapper<Value, Result> {

    private final Method<Value, Result> method;

    public ServerMethodWrapper(Method<Value, Result> method) {
        this.method = method;
    }

    public void handle(DataInputStream dis, DataOutputStream dos) throws IOException {
        Value value = method.readValue(dis);
        Result result = method.apply(value);
        method.writeResult(dos, result);
    }

}
