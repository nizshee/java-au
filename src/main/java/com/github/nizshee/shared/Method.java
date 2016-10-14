package com.github.nizshee.shared;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Method<Value, Result> {
    Result apply(Value value);

    void writeValue(DataOutputStream dos, Value value) throws IOException;

    Value readValue(DataInputStream dis) throws IOException;

    void writeResult(DataOutputStream dos, Result result) throws IOException;

    Result readResult(DataInputStream dis) throws IOException;
}
