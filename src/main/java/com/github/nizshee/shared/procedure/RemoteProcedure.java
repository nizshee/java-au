package com.github.nizshee.shared.procedure;


import com.github.nizshee.shared.util.Dumper;
import com.github.nizshee.shared.exception.WrongDataException;

public abstract class RemoteProcedure<Keeper, Request, Response> {

    public final byte[] executeDump(byte[] ip, Keeper keeper, byte[] bytes) throws WrongDataException {
        Request f = request().restore(bytes);
        Response t = execute(ip, keeper, f);
        return response().dump(t);
    }

    public final byte[] request2Byes(Request request) throws WrongDataException {
        return request().dump(request);
    }

    public final Response bytes2Response(byte[] bytes) throws WrongDataException {
        return response().restore(bytes);
    }

    protected abstract Dumper<Request> request();

    protected abstract Dumper<Response> response();

    protected abstract Response execute(byte[] ip, Keeper keeper, Request request);
}
