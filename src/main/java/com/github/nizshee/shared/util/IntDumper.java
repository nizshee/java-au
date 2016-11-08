package com.github.nizshee.shared.util;


import com.github.nizshee.shared.exception.WrongDataException;

import java.nio.ByteBuffer;

public class IntDumper implements Dumper<Integer> {
    @Override
    public byte[] dump(Integer integer) throws WrongDataException {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(integer);
        return bb.array();
    }

    @Override
    public Integer restore(byte[] bytes) throws WrongDataException {
        if (bytes.length < 4) throw new WrongDataException("Can't read int");
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getInt();
    }
}
