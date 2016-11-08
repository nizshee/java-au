package com.github.nizshee.server.util;


import java.util.Collections;
import java.util.List;

public class UpdateItem {
    public final short port;
    public final List<Integer> identifiers;

    public UpdateItem(short port, List<Integer> identifiers) {
        this.port = port;
        this.identifiers = Collections.unmodifiableList(identifiers);
    }

    @Override
    public int hashCode() {
        return Short.hashCode(port) + identifiers.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UpdateItem) {
            UpdateItem other = (UpdateItem) o;
            return port == other.port && identifiers.equals(other.identifiers);
        }
        return false;
    }
}
