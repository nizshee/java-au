package com.github.nizshee.client.procedure;


import com.github.nizshee.client.ClientKeeper;
import com.github.nizshee.client.util.FilePart;
import com.github.nizshee.shared.procedure.ProcedureRegistry;
import com.github.nizshee.shared.procedure.ProcedureWrapper;
import com.github.nizshee.shared.procedure.RemoteProcedure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("all")
public class ClientRegistry extends ProcedureRegistry<ClientKeeper> {

    public ClientRegistry(ClientKeeper clientKeeper) {
        super(clientKeeper);
    }

    public static final byte STAT_ID = 1;
    public static final byte GET_ID = 2;

    private final static StatProcedure STAT = new StatProcedure();
    private final static GetProcedure GET = new GetProcedure();

    public static final ProcedureWrapper<Integer, Set<Integer>> STATW = new ProcedureWrapper<>(STAT_ID, STAT);
    public static final ProcedureWrapper<FilePart, byte[]> GETW = new ProcedureWrapper<>(GET_ID, GET);

    private final static Map<Byte, RemoteProcedure<ClientKeeper, ?, ?>> procedures;

    static {
        procedures = new HashMap<>();
        procedures.put(STAT_ID, STAT);
        procedures.put(GET_ID, GET);
    }

    @Override
    protected Map<Byte, RemoteProcedure<ClientKeeper, ?, ?>> procedures() {
        return procedures;
    }
}
