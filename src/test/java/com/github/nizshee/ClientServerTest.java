package com.github.nizshee;

import com.github.nizshee.client.Client;
import com.github.nizshee.server.Server;
import com.github.nizshee.server.ServerMethodWrapper;
import com.github.nizshee.shared.Method;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

@SuppressWarnings("all")
public class ClientServerTest {

    private final static int code = 0;
    private final static AtomicBoolean trigger = new AtomicBoolean(false);
    public static final Method<String, String> method = new Method<String, String>() {
        @Override
        public String apply(String s) {
            trigger.set(true);
            return s + s;
        }

        @Override
        public void writeValue(DataOutputStream dos, String s) throws IOException {
            dos.writeUTF(s);
        }

        @Override
        public String readValue(DataInputStream dis) throws IOException {
            return dis.readUTF();
        }

        @Override
        public void writeResult(DataOutputStream dos, String s) throws IOException {
            dos.writeUTF(s);
        }

        @Override
        public String readResult(DataInputStream dis) throws IOException {
            return dis.readUTF();
        }
    };
    private static final Map<Integer, ServerMethodWrapper> handlers;
    static {
        handlers = new HashMap<>();
        handlers.put(code, new ServerMethodWrapper(method));
    }

    @Test
    public void clientServerTest() throws Exception {
        Server server = new Server(InetAddress.getByName("localhost"), 8080);
        server.start(handlers);
        Thread.sleep(500);
        Client client = new Client("localhost", 8080);
        Socket socket = new Socket("localhost", 8080);
        String result = Client.getValue(socket, code, method, "abcd");
        assertEquals("abcdabcd", result);
        socket.close();
        server.stop();
    }
}
