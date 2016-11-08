package com.github.nizshee.shared.procedure;


import com.github.nizshee.shared.exception.WrongDataException;

import static com.github.nizshee.shared.procedure.ProcedureRegistry.addIdentifier;

public class ProcedureWrapper<Request, Response> {
    private final byte id;
    private final RemoteProcedure<?, Request, Response> procedure;

    public ProcedureWrapper(byte id, RemoteProcedure<?, Request, Response> procedure) {
        this.id = id;
        this.procedure = procedure;
    }

    public byte[] dump(Request request) throws WrongDataException {
        return addIdentifier(id, procedure.request2Byes(request));
    }

    public Response restore(byte[] bytes) throws WrongDataException {
        return procedure.bytes2Response(bytes);
    }
}
