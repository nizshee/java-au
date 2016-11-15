package com.github.nizshee.shared;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public interface Method<Request, Response> {
    Response execute(Request request);

    void writeRequest(DataOutputStream dos, Request request) throws IOException;

    Request readRequest(DataInputStream dis) throws IOException;

    void writeResponse(DataOutputStream dos, Response response) throws IOException;

    Response readResult(DataInputStream dis) throws IOException;

    static <Request, Response> Response getValue(Socket socket, int code, Method<Request, Response> method, Request request)
            throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        dos.writeInt(code);
        method.writeRequest(dos, request);
        Response response = method.readResult(dis);
        socket.close();
        return response;
    }
}
