package com.github.nizshee.server.util;


import java.io.Serializable;

public class ClientItem implements Serializable {
    public final byte ip0;
    public final byte ip1;
    public final byte ip2;
    public final byte ip3;
    public final short port;

    public ClientItem(byte ip0, byte ip1, byte ip2, byte ip3, short port) {
        this.ip0 = ip0;
        this.ip1 = ip1;
        this.ip2 = ip2;
        this.ip3 = ip3;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(ip0) + Byte.hashCode(ip1) + Byte.hashCode(ip2) + Byte.hashCode(ip3) + Short.hashCode(port);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClientItem) {
            ClientItem other = (ClientItem) o;
            return ip0 == other.ip0 && ip1 == other.ip1 && ip2 == other.ip2 && ip3 == other.ip3 && port == other.port;
        }
        return false;
    }

    @Override
    public String toString() {
        return ip0 + "." + ip1 + "." + ip2 + "." + ip3 + ":" + port;
    }
}
