package com.github.nizshee.client.procedure;


import com.github.nizshee.client.ClientKeeper;
import com.github.nizshee.client.util.FilePart;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestKeeper implements ClientKeeper {

    public static final int IDENTIFIER = 1942;
    public static final Set<Integer> STAT = new HashSet<>(Arrays.asList(8, 1, 7, 2, 6, 3, 5, 4));
    public static final FilePart FILE_PART = new FilePart(123, 654);
    public static final byte[] BYTES = {0, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    @Override
    public Set<Integer> stat(int identifier) {
        if (identifier != IDENTIFIER) throw new RuntimeException("Not equals.");
        return STAT;
    }

    @Override
    public byte[] get(FilePart part) {
        if (!part.equals(FILE_PART)) throw new RuntimeException("Not equals.");
        return BYTES;
    }
}
