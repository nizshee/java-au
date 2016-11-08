package com.github.nizshee.shared;


import com.github.nizshee.shared.exception.ConnectionClosedException;
import com.github.nizshee.shared.exception.WrongDataException;
import com.github.nizshee.shared.procedure.Registry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Server {

    private ServerSocketChannel ssc;
    private Selector selector;
    private volatile boolean isRunning;
    private final ByteBuffer byteBuffer;
    private final Registry registry;

    public Server(Registry registry) throws IOException {
        isRunning = false;
        byteBuffer = ByteBuffer.allocate(2048);
        this.registry = registry;
    }

    public final void start(InetSocketAddress address) throws IOException {

        ssc = ServerSocketChannel.open();
        ssc.socket().bind(address);
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        isRunning = true;

    }

    public void runNow() throws IOException {
        if (!isRunning) throw new IOException("Server not running.");

        selector.selectNow();
        final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
            final SelectionKey key = iterator.next();

            if (key.isAcceptable()) {
                SocketChannel socketChannel = ssc.accept();
                socketChannel.configureBlocking(false);
                SelectionKey sKey = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                sKey.attach(new ConnectionContext());
            }
            if (key.isValid() && key.isReadable()) {
                ConnectionContext context = (ConnectionContext) key.attachment();
                SocketChannel sc = (SocketChannel) key.channel();

                try {
                    long count = sc.read(byteBuffer);
                    if (count == -1) throw new ConnectionClosedException();
                    byteBuffer.flip();
                    List<byte[]> commands = context.read(byteBuffer);
                    byteBuffer.flip();
                    handle(sc.socket().getInetAddress().getAddress(), context, commands);
                } catch (ConnectionClosedException | IOException | WrongDataException e) {
                    e.printStackTrace();
                    sc.close();
                } finally {
                    byteBuffer.clear();
                }
            }
            if (key.isValid() && key.isWritable()) {
                ConnectionContext context = (ConnectionContext) key.attachment();
                SocketChannel sc = (SocketChannel) key.channel();
                try {
                    context.writeTo(sc);
                } catch (ConnectionClosedException | IOException e) {
                    sc.close();
                    e.printStackTrace();
                }
            }
            iterator.remove();
        }
    }

    public final void stop() {
        try {
            selector.close();
        } catch (IOException ignore) {
        } finally {
            selector = null;
            ssc = null;
            isRunning = false;
        }

    }

    private void handle(byte[] ip, ConnectionContext context, List<byte[]> commands) throws WrongDataException {
        for (byte[] bytes: commands) {
            Optional<byte[]> res = registry.executeDump(ip, bytes);
            if (res.isPresent()) {
                context.write(res.get());
            } else {
                throw new WrongDataException("Wrong command.");
            }
        }
    }
    
}
