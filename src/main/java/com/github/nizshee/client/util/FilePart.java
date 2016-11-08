package com.github.nizshee.client.util;


public class FilePart {
    public final int identifier;
    public final int part;

    public FilePart(int identifier, int part) {
        this.identifier = identifier;
        this.part = part;
    }

    @Override
    public int hashCode() {
        return identifier + part;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FilePart) {
            FilePart other = (FilePart) o;
            return identifier == other.identifier && part == other.part;
        }
        return false;
    }
}
