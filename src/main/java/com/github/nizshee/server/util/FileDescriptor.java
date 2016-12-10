package com.github.nizshee.server.util;


import java.io.Serializable;

public class FileDescriptor implements Serializable {
    public final int id;
    public final String name;
    public final long size;

    public FileDescriptor(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    @Override
    public int hashCode() {
        return id + name.hashCode() + Long.hashCode(size);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileDescriptor) {
            FileDescriptor other = (FileDescriptor) o;
            return id == other.id && name.equals(other.name) && size == other.size;
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + id + " " + name + " " + size + "b";
    }
}
