package com.github.nizshee.shared;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.Assert.*;


public class ConnectionContextTest {

    @Test
    @SuppressWarnings("all")
    public void writeTest() throws Exception {

        ConnectionContext connectionContext = new ConnectionContext();

        byte[] message = {1, 2, 3, 4};
        byte[] buffer = new byte[100];
        byte[] aMessage = new byte[4];
        connectionContext.write(message);

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8082));

        Socket s = new Socket("127.0.0.1", 8082);

        SocketChannel sc = ssc.accept();

        while (!connectionContext.writeTo(sc));
        s.getInputStream().read(buffer);
        System.arraycopy(buffer, 4, aMessage, 0, 4);
        assertArrayEquals(message, aMessage);

        s.close();
        sc.close();
        ssc.close();
    }

    @Test
    @SuppressWarnings("all")
    public void writeMultipleTest() throws Exception {

        ConnectionContext connectionContext = new ConnectionContext();

        byte[] message = {1, 2, 3, 4};
        byte[] message1 = {4, 3, 2, 1};
        byte[] length = {0, 0, 0, 4};
        byte[] buffer = new byte[100];
        byte[] result = new byte[4];
        connectionContext.write(message);
        connectionContext.write(message1);

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8081));

        Socket s = new Socket("127.0.0.1", 8081);

        SocketChannel sc = ssc.accept();

        while (!connectionContext.writeTo(sc));
        s.getInputStream().read(buffer);

        System.arraycopy(buffer, 0, result, 0, 4);
        assertArrayEquals(length, result);
        System.arraycopy(buffer, 4, result, 0, 4);
        assertArrayEquals(message, result);
        System.arraycopy(buffer, 8, result, 0, 4);
        assertArrayEquals(length, result);
        System.arraycopy(buffer, 12, result, 0, 4);
        assertArrayEquals(message1, result);

        s.close();
        sc.close();
        ssc.close();
    }

    @Test
    public void readTest() throws Exception {

        byte[] data = {0, 0, 0, 4, 1, 2, 3, 4};
        byte[] result = {1, 2, 3, 4};

        ByteBuffer bb = ByteBuffer.wrap(data);

        ConnectionContext connectionContext = new ConnectionContext();
        List<byte[]> list = connectionContext.read(bb);
        assertArrayEquals(result, list.get(0));
    }

    @Test
    public void readMultipleTest() throws Exception {

        byte[] data1 = {0, 0, 0, 4, 1, 2, 3, 4, 0, 0};
        byte[] data2 = {0, 4, 4, 3, 2, 1, 0, 0, 0, 4, 3, 4, 2, 1};
        byte[] result1 = {1, 2, 3, 4};
        byte[] result2 = {4, 3, 2, 1};
        byte[] result3 = {3, 4, 2, 1};

        ConnectionContext connectionContext = new ConnectionContext();

        List<byte[]> list = connectionContext.read(ByteBuffer.wrap(data1));
        assertArrayEquals(result1, list.get(0));

        list = connectionContext.read(ByteBuffer.wrap(data2));
        assertArrayEquals(result2, list.get(0));
        assertArrayEquals(result3, list.get(1));
    }

}
