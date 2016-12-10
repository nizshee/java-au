package com.github.nizshee.client;


import com.github.nizshee.client.util.FilePart;
import com.github.nizshee.server.util.FileDescriptor;

import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class ClientKeeperImpl implements ClientKeeper, Serializable {

    public static final int PART_SIZE = 1024;

    private Map<Integer, FileDescriptor> info = new HashMap<>();
    private Map<Integer, Set<Integer>> downloaded = new HashMap<>();
    private Map<Integer, Set<Integer>> toDownload = new HashMap<>();


    @Override
    public Set<Integer> stat(int identifier) {
        return downloaded.keySet().stream()
                .filter(id -> id == identifier)
                .findFirst()
                .map(downloaded::get)
                .orElse(new HashSet<>());
    }

    @Override
    public byte[] get(FilePart filePart) {
        Optional<FileDescriptor> optional = getListItem(filePart.identifier);
        if (!optional.isPresent()) return new byte[0];
        FileDescriptor item = optional.get();
        try (RandomAccessFile raf = new RandomAccessFile(item.name, "rw")) {
            if (raf.length() != item.size) throw new Exception("file has different size");
            if (partCount(item.size) < filePart.part) return new byte[0];
            long seek = (long) filePart.part * PART_SIZE;
            raf.seek(seek);
            byte[] bytes = new byte[partSize(item.size, filePart.part)];
            raf.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public List<Integer> update() {
        return downloaded.keySet().stream()
                .filter(k -> !downloaded.get(k).isEmpty())
                .collect(Collectors.toList());
    }

    public void upload(FileDescriptor item) {
        info.put(item.id, item);
        downloaded.put(item.id, IntStream.range(0, partCount(item.size)).mapToObj(i -> i).collect(Collectors.toSet()));
        toDownload.put(item.id, new HashSet<>());
    }

    public void put(FilePart filePart, byte[] bytes) {
        Optional<FileDescriptor> optional = getListItem(filePart.identifier);
        if (!optional.isPresent()) {
            System.err.println("can't find id");
            return;
        }
        FileDescriptor item = optional.get();
        if (filePart.part > partCount(item.size)) {
            System.err.println("wrong size");
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(item.name, "rw")) {
            if (raf.length() != item.size) throw new Exception("file has different size");
            long seek = (long) filePart.part * PART_SIZE;
            raf.seek(seek);
            raf.write(bytes);
            toDownload.get(filePart.identifier).remove(filePart.part);
            downloaded.get(filePart.identifier).add(filePart.part);
            if (toDownload.get(filePart.identifier).isEmpty()) {
                System.out.println("file " + filePart.identifier + " is ready");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get(FileDescriptor item) {
        try (RandomAccessFile raf = new RandomAccessFile(item.name, "rw")) {
            for (int i = 0; i < partCount(item.size); ++i) {
                byte[] bytes = new byte[partSize(item.size, i)];
                raf.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info.put(item.id, item);
        downloaded.put(item.id, new HashSet<>());
        toDownload.put(item.id, IntStream.range(0, partCount(item.size)).mapToObj(i -> i).collect(Collectors.toSet()));
    }

    public Map<Integer, Set<Integer>> getToDownload() {
        return new HashMap<>(toDownload);
    }

    private Optional<FileDescriptor> getListItem(int identifier) {
        return info.containsKey(identifier) ? Optional.of(info.get(identifier)) : Optional.empty();
    }

    private int partCount(long size) {
        return (int) (size / PART_SIZE) + (size % PART_SIZE == 0 ? 0 : 1);
    }

    private int partSize(long size, int number) {
        if (number < partCount(size) - 1) return PART_SIZE;
        else {
            return size % PART_SIZE == 0 ? PART_SIZE : (int) (size % PART_SIZE);
        }
    }
}
