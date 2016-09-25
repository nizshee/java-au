package com.github.nizshee.workspace;


import com.github.nizshee.exception.WorkspaceException;

import java.util.*;

public class InMemoryWorkspace implements Workspace {

    private final Map<String, List<String>> files;

    public InMemoryWorkspace(Map<String, List<String>> files) {
        this.files = new HashMap<>(files);
    }

    @Override
    public Set<String> getFiles() throws WorkspaceException {
        return new HashSet<>(files.keySet());
    }

    @Override
    public List<String> get(String fileName) throws WorkspaceException {
        if (!files.containsKey(fileName)) throw new WorkspaceException("File not found");
        return files.get(fileName);
    }

    @Override
    public void create(String fileName, List<String> content) throws WorkspaceException {
        if (files.containsKey(fileName)) throw new WorkspaceException("File " + fileName + " already exists");
        files.put(fileName, content);
    }

    @Override
    public void remove(String fileName) throws WorkspaceException {
        if (!files.containsKey(fileName)) throw new WorkspaceException("File not found");
        files.remove(fileName);
    }

    @Override
    public void change(String fileName, List<String> content) throws WorkspaceException {
        if (!files.containsKey(fileName)) throw new WorkspaceException("File not found");
        files.put(fileName, content);
    }
}
