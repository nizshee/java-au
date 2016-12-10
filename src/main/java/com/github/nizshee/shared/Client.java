package com.github.nizshee.shared;


import com.github.nizshee.shared.exception.ConnectionClosedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

public class Client {

    private SocketChannel socketChannel = null;
    private ConnectionContext context = null;
    private Selector selector = null;
    private volatile boolean isRunning = false;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

    public void connect(InetSocketAddress address) throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.connect(address);
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        context = new ConnectionContext();
        isRunning = true;
    }

    public void disconnect() throws IOException {
        if (selector != null) selector.close();
        if (socketChannel != null) socketChannel.close();

        isRunning = false;
        context = null;
        selector = null;
        socketChannel = null;
    }

    public void write(byte[] bytes) throws IOException {
        if (!isRunning) throw new IOException("TorrentClient not connected.");
        context.write(bytes);
    }

    public LinkedList<byte[]> runNow() throws IOException {
        if (!isRunning) throw new IOException("TorrentClient not connected.");

        selector.selectNow();
        final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        LinkedList<byte[]> result = new LinkedList<>();

        while (iterator.hasNext()) {
            final SelectionKey key = iterator.next();

            if (key.isValid() && key.isReadable()) {
                try {
                    long count = socketChannel.read(byteBuffer);
                    if (count == -1) throw new ConnectionClosedException();
                    byteBuffer.flip();
                    result.addAll(context.read(byteBuffer));
                    byteBuffer.flip();
                } catch (ConnectionClosedException | IOException e) {
                    disconnect();
                } finally {
                    byteBuffer.clear();
                }
            }
            if (key.isValid() && key.isWritable()) {
                try {
                    context.writeTo(socketChannel);
                } catch (ConnectionClosedException | IOException e) {
                    disconnect();
                }
            }
            iterator.remove();
        }

        return result;
    }



}
