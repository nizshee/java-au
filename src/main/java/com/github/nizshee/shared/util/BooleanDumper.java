package com.github.nizshee.shared.util;


import com.github.nizshee.shared.exception.WrongDataException;

public class BooleanDumper implements Dumper<Boolean> {
    @Override
    public byte[] dump(Boolean aBoolean) throws WrongDataException {
        byte[] bytes = new byte[1];
        bytes[0] = aBoolean ? (byte) 1 : (byte) 0;
        return bytes;
    }

    @Override
    public Boolean restore(byte[] bytes) throws WrongDataException {
        if (bytes.length < 1) throw new WrongDataException("Can't read Boolean.");
        if (bytes[0] == (short) 0) return false;
        if (bytes[0] == (short) 1) return true;
        throw new WrongDataException("Can't read Boolean.");
    }
}
