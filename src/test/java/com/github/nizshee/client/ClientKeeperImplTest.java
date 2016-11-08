package com.github.nizshee.client;


import static org.junit.Assert.*;

import com.github.nizshee.client.util.FilePart;
import com.github.nizshee.server.util.FileDescriptor;
import org.junit.Test;

import java.io.File;
import java.util.Random;

public class ClientKeeperImplTest {

    @Test
    @SuppressWarnings("all")
    public void putGetTest() throws Exception {
        String fileName = "test_file";
        File file = new File(fileName);
        ClientKeeperImpl clientKeeper = new ClientKeeperImpl();
        byte[] data1 = new byte[ClientKeeperImpl.PART_SIZE];
        byte[] data2 = new byte[ClientKeeperImpl.PART_SIZE / 2];
        long totalSize = ClientKeeperImpl.PART_SIZE + (ClientKeeperImpl.PART_SIZE / 2);
        new Random().nextBytes(data1);
        new Random().nextBytes(data2);

        assertFalse(file.exists());

        clientKeeper.get(new FileDescriptor(1, fileName, totalSize));

        assertTrue(file.exists());
        assertEquals(totalSize, file.length());

        clientKeeper.put(new FilePart(1, 0), data1);
        assertArrayEquals(data1, clientKeeper.get(new FilePart(1, 0)));

        clientKeeper.put(new FilePart(1, 1), data2);
        assertArrayEquals(data2, clientKeeper.get(new FilePart(1, 1)));

        file.delete();
    }


}
