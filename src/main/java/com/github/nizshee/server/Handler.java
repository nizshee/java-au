package com.github.nizshee.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Handler {

    void handle(DataInputStream dis, DataOutputStream dos) throws IOException;

}
