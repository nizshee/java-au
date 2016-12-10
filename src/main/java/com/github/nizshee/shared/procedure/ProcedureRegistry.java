package com.github.nizshee.shared.procedure;


import com.github.nizshee.shared.exception.WrongDataException;


import java.util.Map;
import java.util.Optional;

@SuppressWarnings("all")
public abstract class ProcedureRegistry<Keeper> implements Registry {

    private final Keeper keeper;

    public ProcedureRegistry(Keeper keeper) {
        this.keeper = keeper;
    }

    @Override
    public Optional<byte[]> executeDump(byte[] ip, byte[] bytes) {
        try {
            byte identifier = getIdentifier(bytes);
            if (!procedures().containsKey(identifier)) {
                return Optional.empty();
            }
            RemoteProcedure<Keeper, ?, ?> remoteProcedure = procedures().get(identifier);
            byte[] rawBytes = getRawData(bytes);
            return Optional.of(remoteProcedure.executeDump(ip, keeper, rawBytes));
        } catch (WrongDataException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    protected abstract Map<Byte, RemoteProcedure<Keeper, ?, ?>> procedures();

    protected static byte[] addIdentifier(byte identifier, byte[] bytes) throws WrongDataException {
        byte[] nBytes = new byte[bytes.length + 1];
        nBytes[0] = identifier;
        System.arraycopy(bytes, 0, nBytes, 1, bytes.length);
        return nBytes;
    }

    private static byte getIdentifier(byte[] bytes) throws WrongDataException {
        if (bytes.length < 1) throw new WrongDataException("Empty bytes");
        return bytes[0];
    }

    private static byte[] getRawData(byte[] bytes) throws WrongDataException {
        if (bytes.length < 1) throw new WrongDataException("Empty bytes");
        byte[] nBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, nBytes, 0, bytes.length - 1);
        return nBytes;
    }
}
