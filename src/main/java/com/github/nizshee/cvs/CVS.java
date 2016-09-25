package com.github.nizshee.cvs;

import com.github.nizshee.CommitMessage;
import com.github.nizshee.exception.WorkspaceException;
import com.github.nizshee.exception.StateException;
import com.github.nizshee.index.Index;
import com.github.nizshee.node.Commit;
import com.github.nizshee.node.Init;
import com.github.nizshee.node.Merge;
import com.github.nizshee.node.Node;
import com.github.nizshee.state.State;
import com.github.nizshee.workspace.Workspace;

import java.util.*;

public class CVS {

    private String currentHash;
    private State current;
    private final Workspace workspace;
    private final Index index;

    public CVS(Workspace workspace, Index index) throws StateException {
        this.workspace = workspace;
        this.index = index;
    }

    public String commit(String comment) throws WorkspaceException, StateException {
        Set<String> toRemove = current.removed(workspace);
        Set<String> toChange = current.changed(workspace);
        Set<String> toCreate = current.created(workspace);

        if (toCreate.isEmpty() && toRemove.isEmpty() && toChange.isEmpty())
            throw new WorkspaceException("No changes");

        Set<Commit.Diff> changes = new HashSet<>();

        try {
            for (String fileName: toRemove) {
                changes.add(new Commit.Remove(fileName));
            }

            for (String fileName: toCreate) {
                changes.add(new Commit.Create(fileName, workspace.get(fileName)));
            }

            for (String fileName: toChange) {
                changes.add(new Commit.Change(fileName, workspace.get(fileName)));
            }

        } catch (WorkspaceException ignore) {
            ignore.printStackTrace();
        }

        Node node = new Commit(comment, currentHash, changes);
        currentHash = index.addNode(node);
        current = node.change(Collections.singletonList(current));
        current.changeWorkspace(workspace);
        return currentHash;
    }

    public String mergeWith(String hash) throws WorkspaceException, StateException {
        Node node = new Merge(currentHash, hash);
        currentHash = index.addNode(node);
        List<State> states = new LinkedList<>();
        states.add(current);
        states.add(index.getState(hash));
        current = node.change(states);
        current.changeWorkspace(workspace);
        return currentHash;
    }

    public void checkout(String hash) throws WorkspaceException, StateException {
        if (!saved()) throw new WorkspaceException("Need commit before checkout.");
        State state = index.getState(hash);
        state.changeWorkspace(workspace);
        current = state;
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
        for (String dependency: dependencies) {
            result.addAll(log(dependency));
        }
        result.add(node.message(hash));
        return result;
    }

    public String create() {
        Node node = new Init();
        return index.addNode(node);
    }

    public void change(String hash) throws WorkspaceException, StateException {
        current = index.getState(hash);
        currentHash = hash;
    }

    public Set<String> changed() throws WorkspaceException {
        return current.changed(workspace);
    }

    public Set<String> created() throws WorkspaceException {
        return current.created(workspace);
    }

    public Set<String> removed() throws WorkspaceException {
        return current.removed(workspace);
    }

    @SuppressWarnings("all")
    public boolean saved() throws WorkspaceException {
        return changed().isEmpty() && created().isEmpty() && removed().isEmpty();
    }
}
