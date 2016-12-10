package com.github.nizshee.server.util;


public class UploadItem {
    public final String name;
    public final long size;

    public UploadItem(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + Long.hashCode(size);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UploadItem) {
            UploadItem other = (UploadItem) o;
            return name.equals(other.name) && size == other.size;
        }
        return false;
    }
}
