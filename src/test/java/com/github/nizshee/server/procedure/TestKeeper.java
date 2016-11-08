package com.github.nizshee.server.procedure;


import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.ServerKeeper;
import com.github.nizshee.server.util.UpdateItem;
import com.github.nizshee.server.util.UploadItem;

import java.util.Arrays;
import java.util.List;

public class TestKeeper implements ServerKeeper {

    public final static List<FileDescriptor> LIST = Arrays.asList(new FileDescriptor(0, "0", 0), new FileDescriptor(1, "1", 1000),
            new FileDescriptor(2, "2", 2000));
    public final static int IDENTIFIER = 1033;
    public final static UploadItem UPLOAD_ITEM = new UploadItem("123", 123456);
    public final static List<ClientItem> SOURCES = Arrays.asList(
            new ClientItem((byte) 1, (byte) 2, (byte) 3, (byte) 4, (short) 2),
            new ClientItem((byte) 4, (byte) 3, (byte) 2, (byte) 1, (short) 4),
            new ClientItem((byte) 12, (byte) 21, (byte) 11, (byte) 22, (short) 6));
    public final static UpdateItem UPDATE_ITEM = new UpdateItem((short) 1423, Arrays.asList(1, 2, 3, 4, 5));

    @Override
    public List<FileDescriptor> list() {
        return LIST;
    }

    @Override
    public int upload(UploadItem item) {
        if (!item.equals(UPLOAD_ITEM)) throw new RuntimeException("Not equals");
        return IDENTIFIER;
    }

    @Override
    public List<ClientItem> sources(int identifier) {
        if (identifier != IDENTIFIER) throw new RuntimeException("Not equals");
        return SOURCES;
    }

    @Override
    public boolean update(byte[] ip, UpdateItem item) {
        if (!item.equals(UPDATE_ITEM)) throw new RuntimeException("Not equals");
        return true;
    }
}
