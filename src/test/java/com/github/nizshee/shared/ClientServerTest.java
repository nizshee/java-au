package com.github.nizshee.shared;


import com.github.nizshee.shared.procedure.Registry;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ClientServerTest {

    private static Registry echo = (ip, bytes) -> {
        byte[] local = {127, 0, 0, 1};
        assertArrayEquals(local, ip);
        return Optional.of(bytes);
    };

    @Test
    public void clientServerTest() throws Exception {
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 8};
        InetSocketAddress address = new InetSocketAddress("localhost", 8083);

        Server simpleServer = new Server(echo);

        simpleServer.start(address);
        Client client = new Client();
        client.connect(address);
        client.write(data);

        List<byte[]> result = retrieveData(simpleServer, client);

        assertArrayEquals(data, result.get(0));

        client.disconnect();
        simpleServer.stop();
    }

    @Test
    public void clientServerMultipleTest() throws Exception {
        byte[] data1 = {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] data2 = {4, 3, 2, 1};

        InetSocketAddress address = new InetSocketAddress("localhost", 8084);

        Server simpleServer = new Server(echo);

        simpleServer.start(address);
        Client client = new Client();
        client.connect(address);

        client.write(data1);
        client.write(data2);

        List<byte[]> result = retrieveData(simpleServer, client);

        assertArrayEquals(data1, result.get(0));
        assertArrayEquals(data2, result.get(1));

        client.write(data2);
        client.write(data1);

        result = retrieveData(simpleServer, client);

        assertArrayEquals(data1, result.get(1));
        assertArrayEquals(data2, result.get(0));

        client.disconnect();
        simpleServer.stop();
    }

    private static List<byte[]> retrieveData(Server server, Client client)
            throws IOException, InterruptedException {
        client.runNow(); // write data

        Thread.sleep(100);

        server.runNow(); // read data
        server.runNow(); // write data

        Thread.sleep(100);

        return client.runNow(); // read data
    }
}
