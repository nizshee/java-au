package com.github.nizshee.shared;


public class RemoteFile {

    public final boolean isDirectory;
    public final String name;

    public RemoteFile(String name, boolean isDirectory) {
        this.isDirectory = isDirectory;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(isDirectory) + name.hashCode() % 34;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RemoteFile && ((RemoteFile) o).name.equals(name) && ((RemoteFile) o).isDirectory == isDirectory;
    }

    @Override
    public String toString() {
        return name;
    }
}
