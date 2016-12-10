package com.github.nizshee.server.procedure;


import com.github.nizshee.server.ServerKeeper;
import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.util.UpdateItem;
import com.github.nizshee.server.util.UploadItem;
import com.github.nizshee.shared.procedure.ProcedureRegistry;
import com.github.nizshee.shared.procedure.ProcedureWrapper;
import com.github.nizshee.shared.procedure.RemoteProcedure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerRegistry extends ProcedureRegistry<ServerKeeper> {

    public ServerRegistry(ServerKeeper keeper) {
        super(keeper);
    }

    private static final byte LIST_ID = 1;
    private static final byte UPLOAD_ID = 2;
    private static final byte SOURCES_ID = 3;
    private static final byte UPDATE_ID = 4;

    private static final ListProcedure LIST = new ListProcedure();
    private static final UploadProcedure UPLOAD = new UploadProcedure();
    private static final SourcesProcedure SOURCES = new SourcesProcedure();
    private static final UpdateProcedure UPDATE = new UpdateProcedure();

    public static final ProcedureWrapper<Object, List<FileDescriptor>> LISTW = new ProcedureWrapper<>(LIST_ID, LIST);
    public static final ProcedureWrapper<UploadItem, Integer> UPLOADW = new ProcedureWrapper<>(UPLOAD_ID, UPLOAD);
    public static final ProcedureWrapper<Integer, List<ClientItem>> SOURCESW = new ProcedureWrapper<>(SOURCES_ID, SOURCES);
    public static final ProcedureWrapper<UpdateItem, Boolean> UPDATEW = new ProcedureWrapper<>(UPDATE_ID, UPDATE);


    private final static Map<Byte, RemoteProcedure<ServerKeeper, ?, ?>> procedures;

    static {
        procedures = new HashMap<>();
        procedures.put(LIST_ID, LIST);
        procedures.put(UPLOAD_ID, UPLOAD);
        procedures.put(SOURCES_ID, SOURCES);
        procedures.put(UPDATE_ID, UPDATE);
    }

    @Override
    protected Map<Byte, RemoteProcedure<ServerKeeper, ?, ?>> procedures() {
        return procedures;
    }
}
