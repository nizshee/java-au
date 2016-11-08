package com.github.nizshee.server;


import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.util.UpdateItem;
import com.github.nizshee.server.util.UploadItem;

import java.util.List;

public interface ServerKeeper {

    List<FileDescriptor> list();

    int upload(UploadItem item);

    List<ClientItem> sources(int identifier);

    boolean update(byte[] ip, UpdateItem item);
}
