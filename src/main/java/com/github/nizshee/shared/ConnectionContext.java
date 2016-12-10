package com.github.nizshee.shared;


import com.github.nizshee.shared.exception.ConnectionClosedException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("all")
public class ConnectionContext {
    private LinkedList<ByteBuffer> toWrite;
    private Buffer current;

    public ConnectionContext() {
        toWrite = new LinkedList<>();
        current = new Buffer();
    }

    public List<byte[]> read(ByteBuffer bb) {
        LinkedList<byte[]> list = new LinkedList<>();
        while (bb.hasRemaining()) {
            current.read(bb);
            if (current.isComplete()) {
                list.add(current.getBytes());
                current = new Buffer();
            }
        }
        return list;
    }

    public boolean writeTo(GatheringByteChannel bc) throws IOException, ConnectionClosedException {
        if (toWrite.isEmpty()) return true;
        ByteBuffer[] bbs = new ByteBuffer[toWrite.size()];
        long count = bc.write(toWrite.toArray(bbs));
        if (count == -1) throw new ConnectionClosedException();
        Iterator<ByteBuffer> i = toWrite.iterator();
        while (i.hasNext()) {
            ByteBuffer bb = i.next();
            if (bb.hasRemaining()) {
                return false;
            } else {
                i.remove();
            }
        }
        return toWrite.isEmpty();
    }

    public void write(byte[] bytes) {
        byte[] w = new byte[bytes.length + 4];
        w[0] = (byte)(bytes.length >>> 24);
        w[1] = (byte)(bytes.length >>> 16);
        w[2] = (byte)(bytes.length >>> 8);
        w[3] = (byte)(bytes.length);
        System.arraycopy(bytes, 0, w, 4, bytes.length);
        toWrite.add(ByteBuffer.wrap(w));
    }


    private final class Buffer {
        private short sizeCount;
        private int size;
        private byte[] bytes;
        private int read;

        Buffer() {
            this.size = 0;
            this.sizeCount = 0;
            this.bytes = null;
            this.read = 0;
        }

        boolean isComplete() {
            return sizeCount > 3 && read == size;
        }

        void read(ByteBuffer bb) {
            while (bb.hasRemaining()) {
                if (sizeCount < 4) {
                    int signedByte = bb.get() & 0xff;
                    if (sizeCount == 0) {
                        size += signedByte << 24;
                    } else if (sizeCount == 1) {
                        size += signedByte << 16;
                    } else if (sizeCount == 2) {
                        size += signedByte << 8;
                    } else if (sizeCount == 3) {
                        size += signedByte;
                        bytes = new byte[size];
                    }
                    sizeCount += 1;
                } else if (read < size) {
                    int toRead = Math.min(bb.remaining(), size - read);
                    bb.get(bytes, read, toRead);
                    read += toRead;
                    if (read == size) return;
                }
            }
        }

        byte[] getBytes() {
            return bytes;
        }
    }
}
