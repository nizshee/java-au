package com.github.nizshee.client;


import com.github.nizshee.client.util.FilePart;

import java.util.Set;

public interface ClientKeeper {

    Set<Integer> stat(int identifier);

    byte[] get(FilePart part);
}
