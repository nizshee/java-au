package com.github.nizshee.state;


import com.github.nizshee.workspace.Workspace;
import com.github.nizshee.exception.WorkspaceException;
import com.github.nizshee.exception.StateException;

import java.util.*;
import java.util.stream.Collectors;

public class State {

    private Map<String, List<String>> files;

    public State() {
        files = new HashMap<>();
    }

    public void create(String fileName, List<String> content) throws StateException {
        if (files.containsKey(fileName)) throw new StateException("File " + fileName + " exists.");
        files.put(fileName, content);
    }

    public void put(String fileName, List<String> content) throws StateException {
        if (!files.containsKey(fileName)) throw new StateException("File " + fileName + " not found.");
        files.put(fileName, content);
    }

    public List<String> get(String fileName) throws StateException {
        if (!files.containsKey(fileName)) throw new StateException("File " + fileName + " not found.");
        return files.get(fileName);
    }

    public void remove(String fileName) throws StateException {
        files.remove(fileName);
    }

    public Set<String> changed(Workspace workspace) throws WorkspaceException {
        Set<String> wFiles = workspace.getFiles();
        Set<String> result = new HashSet<>();
        for (String filename: files.keySet()) {
            if (wFiles.contains(filename) && !workspace.get(filename).equals(files.get(filename)))
                result.add(filename);
        }
        return result;
    }

    public Set<String> changed(State state) {
        return files.keySet().stream().filter(state.files.keySet()::contains)
                .filter(fileName -> !files.get(fileName).equals(state.files.get(fileName))).collect(Collectors.toSet());
    }

    public Set<String> created(Workspace workspace) throws WorkspaceException {
        Set<String> wFiles = workspace.getFiles();
        return wFiles.stream().filter(fileName -> !files.keySet().contains(fileName)).collect(Collectors.toSet());
    }

    public Set<String> created(State state) {
        return state.files.keySet().stream().filter(fileName -> !files.keySet().contains(fileName))
                .collect(Collectors.toSet());
    }

    public Set<String> removed(Workspace workspace) throws WorkspaceException {
        Set<String> wFiles = workspace.getFiles();
        return files.keySet().stream().filter(fileName -> !wFiles.contains(fileName)).collect(Collectors.toSet());
    }

    public void changeWorkspace(Workspace workspace) throws WorkspaceException {
        Set<String> toCreate = removed(workspace);
        Set<String> toChange = changed(workspace);
        Set<String> toRemove = created(workspace);

        for (String fileName: toCreate) {
            workspace.create(fileName, files.get(fileName));
        }

        for (String fileName: toChange) {
            workspace.change(fileName, files.get(fileName));
        }

        for (String fileName: toRemove) {
            workspace.remove(fileName);
        }
    }
}
