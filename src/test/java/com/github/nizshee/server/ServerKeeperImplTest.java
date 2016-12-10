package com.github.nizshee.server;


import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.util.UpdateItem;
import com.github.nizshee.server.util.UploadItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ServerKeeperImplTest {

    @Test
    public void listTest() throws Exception {
        ServerKeeperImpl serverKeeper = new ServerKeeperImpl();

        int i0 = serverKeeper.upload(new UploadItem("0", 0));
        int i1 = serverKeeper.upload(new UploadItem("1", 1));
        int i2 = serverKeeper.upload(new UploadItem("2", 2));
        int i3 = serverKeeper.upload(new UploadItem("3", 3));

        List<FileDescriptor> res = Arrays.asList(new FileDescriptor(i0, "0", 0), new FileDescriptor(i1, "1", 1),
                new FileDescriptor(i2, "2", 2), new FileDescriptor(i3, "3", 3));

        assertEquals(res, serverKeeper.list());
    }

    @Test
    public void clearOldTest() throws Exception {
        ServerKeeperImpl serverKeeper = new ServerKeeperImpl();

        byte[] port1 = {1, 2, 3, 4};
        byte[] port2 = {4, 3, 2, 1};
        int i0 = serverKeeper.upload(new UploadItem("0", 0));

        serverKeeper.update(port1, new UpdateItem((short) 1, Collections.singletonList(i0)));

        Thread.sleep(1000);

        serverKeeper.update(port2, new UpdateItem((short) 2, Collections.singletonList(i0)));

        serverKeeper.clearOld(LocalDateTime.now().plusMinutes(4));

        assertEquals(
                Arrays.asList(
                        new ClientItem((byte) 1, (byte) 2, (byte) 3, (byte) 4, (short) 1),
                        new ClientItem((byte) 4, (byte) 3, (byte) 2, (byte) 1, (short) 2)
                ), serverKeeper.sources(i0)
        );

        serverKeeper.clearOld(LocalDateTime.now().plusMinutes(4).plusSeconds(59));

        assertEquals(
                Collections.singletonList(new ClientItem((byte) 4, (byte) 3, (byte) 2, (byte) 1, (short) 2)),
                serverKeeper.sources(i0)
        );

        serverKeeper.clearOld(LocalDateTime.now().plusMinutes(5));

        assertEquals(Collections.emptyList(), serverKeeper.sources(i0));
    }
}
