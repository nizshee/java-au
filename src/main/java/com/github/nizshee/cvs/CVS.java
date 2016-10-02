package com.github.nizshee.cvs;

import com.github.nizshee.message.CommitMessage;
import com.github.nizshee.exception.CVSException;
import com.github.nizshee.exception.WorkspaceException;
import com.github.nizshee.exception.StateException;
import com.github.nizshee.index.Index;
import com.github.nizshee.node.Commit;
import com.github.nizshee.node.Init;
import com.github.nizshee.node.Merge;
import com.github.nizshee.node.Node;
import com.github.nizshee.state.State;
import com.github.nizshee.workspace.Workspace;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class CVS implements Serializable {

    private String currentHash;
    private State current;
    private State stage;
    private final Workspace workspace;
    private final Index index;
    private final Set<String> ignorePaths;

    public CVS(Workspace workspace, Index index, List<String> ignore) throws StateException {
        this.workspace = workspace;
        this.index = index;
        this.ignorePaths = new HashSet<>(ignore);
    }

    public String commit(String comment) throws WorkspaceException, StateException {
        Set<String> toRemove = current.created(stage);
        Set<String> toChange = current.changed(stage);
        Set<String> toCreate = current.removed(stage);

        if (toCreate.isEmpty() && toRemove.isEmpty() && toChange.isEmpty())
            throw new WorkspaceException("No changes.");

        Set<Commit.Diff> changes = new HashSet<>();

        try {
            for (String fileName : toRemove) {
                changes.add(new Commit.Remove(fileName));
            }

            for (String fileName : toCreate) {
                changes.add(new Commit.Create(fileName, workspace.get(fileName)));
            }

            for (String fileName : toChange) {
                changes.add(new Commit.Change(fileName, workspace.get(fileName)));
            }

        } catch (WorkspaceException ignore) {
            ignore.printStackTrace();
        }

        Node node = new Commit(comment, currentHash, changes);
        currentHash = index.addNode(node);
        current = node.change(Collections.singletonList(current));

        return currentHash;
    }

    public String mergeWith(String hash) throws WorkspaceException, StateException, CVSException {
        Node node = new Merge(currentHash, hash);
        List<State> states = Arrays.asList(current, index.getState(hash));
        current = node.change(states);
        stage = current.copy();
        currentHash = index.addNode(node);
        current.changeWorkspace(workspace);
        return currentHash;
    }

    public void checkout(String hash) throws WorkspaceException, StateException, CVSException {
        checkCompatible(hash);
        State state = index.getState(hash);
        state.changeWorkspace(workspace);
        current = state;
        stage = current.copy();
        currentHash = hash;
    }

    public Set<CommitMessage> log() throws StateException {
        return log(currentHash);
    }

    @SuppressWarnings("all")
    public Set<CommitMessage> log(String hash) throws StateException {
        Node node = index.getNode(hash);
        List<String> dependencies = node.getDependencies();
        Set<CommitMessage> result = new HashSet<>();
        for (String dependency : dependencies) {
            result.addAll(log(dependency));
        }
        result.add(node.message(hash));
        return result;
    }

    public void change(String hash) throws WorkspaceException, StateException {
        current = index.getState(hash);
        stage = current.copy();
        currentHash = hash;
    }

    public Set<String> changedToCommit() throws WorkspaceException {
        return stage.changed(current).stream()
                .filter(path -> !ignorePaths.contains(path))
                .collect(Collectors.toSet());
    }

    public Set<String> createdToCommit() throws WorkspaceException {
        return stage.created(current).stream()
                .filter(path -> !ignorePaths.contains(path))
                .collect(Collectors.toSet());
    }

    public Set<String> removedToCommit() throws WorkspaceException {
        return stage.removed(current).stream()
                .filter(path -> !ignorePaths.contains(path))
                .collect(Collectors.toSet());
    }

    public Set<String> notTracked() throws WorkspaceException {
        return workspace.getFiles().stream()
                .filter(path -> !ignorePaths.contains(path))
                .filter(path -> !current.contains(path) && !stage.contains(path))
                .collect(Collectors.toSet());
    }

    public Set<String> created() throws WorkspaceException {
        return stage.created(workspace).stream()
                .filter(current::contains)
                .filter(path -> !ignorePaths.contains(path))
                .collect(Collectors.toSet());
    }

    public Set<String> changed() throws WorkspaceException {
        return stage.changed(workspace).stream()
                .filter(path -> !ignorePaths.contains(path))
                .collect(Collectors.toSet());
    }

    public Set<String> removed() throws WorkspaceException {
        return stage.removed(workspace).stream()
                .filter(path -> !ignorePaths.contains(path))
                .collect(Collectors.toSet());
    }

    public void add(String fileName) throws WorkspaceException, StateException, CVSException {
        Set<String> workspaceFiles = workspace.getFiles();
        if (stage.contains(fileName) && workspaceFiles.contains(fileName)) {
            stage.put(fileName, workspace.get(fileName));
        } else if (stage.contains(fileName) && !workspaceFiles.contains(fileName)) {
            stage.remove(fileName);
        } else if (!stage.contains(fileName) && workspaceFiles.contains(fileName)) {
            stage.create(fileName, workspace.get(fileName));
        } else {
            throw new CVSException("No file " + fileName + " found.");
        }
    }

    public void reset(String fileName) throws StateException {
        stage.syncFile(fileName, current);
    }

    public void clean() throws WorkspaceException {
        for (String fileName : notTracked()) {
            workspace.remove(fileName);
        }
    }

    public void rm(String fileName) throws WorkspaceException, StateException {
        if (stage.contains(fileName))
            stage.remove(fileName);
        if (workspace.getFiles().contains(fileName))
            workspace.remove(fileName);
    }

    public void init() throws StateException, CVSException {
        if (currentHash != null) throw new CVSException("Already init.");
        Node node = new Init();
        currentHash = index.addNode(node);
        current = node.change(Collections.emptyList());
        stage = current.copy();
    }

    public String current() {
        return currentHash;
    }

    @SuppressWarnings("all")
    public boolean saved() throws WorkspaceException {
        return changedToCommit().isEmpty() && createdToCommit().isEmpty() && removedToCommit().isEmpty();
    }

    private boolean checkCompatible(String hash) throws StateException, WorkspaceException, CVSException {
        State state = index.getState(hash);
        Set<String> bad = notTracked().stream().filter(state::contains).collect(Collectors.toSet());
        if (!bad.isEmpty())
            throw new CVSException("" + hash + " contains untrack files.");
        if (!changedToCommit().isEmpty() || !removedToCommit().isEmpty() || !createdToCommit().isEmpty())
            throw new CVSException("Not committed changes.");
        if (changed().stream().filter(state::contains).findAny().isPresent())
            throw new CVSException("Files presented in " + hash + "changed.");
        if (created().stream().filter(state::contains).findAny().isPresent())
            throw new CVSException("Files presented in " + hash + "created.");
        return true;
    }
}
