package com.github.nizshee.node;


import com.github.nizshee.CommitMessage;
import com.github.nizshee.exception.StateException;
import com.github.nizshee.state.State;

import java.io.Serializable;
import java.util.*;

public class Commit implements Node {

    private final String prev;
    private final Set<Diff> changes;
    private final String message;

    public Commit(String message, String prev, Set<Diff> changes) {
        this.message = message;
        this.prev = prev;
        this.changes = changes;
    }

    @Override
    public CommitMessage message(String hash) {
        return new CommitMessage(message, hash);
    }

    @Override
    public List<String> getDependencies() {
        return Collections.singletonList(prev);
    }

    @Override
    public State change(List<State> states) throws StateException {
        if (states.size() != 1) throw new StateException("Need only one changeCurrent for hash");
        State state = states.get(0);
        for (Diff diff: changes) {
            diff.change(state);
        }
        return state;
    }


    public interface Diff extends Serializable {
        void change(State state) throws StateException;
    }

    public static class Change implements Diff {

        private final String fileName;
        private final List<String> post;

        public Change(String fileName, List<String> post) {
            this.fileName = fileName;
            this.post = post;
        }

        @Override
        public void change(State state) throws StateException {
            state.put(fileName, post);
        }
    }

    public static class Create implements Diff {

        private final String fileName;
        private final List<String> content;

        public Create(String fileName, List<String> content) {
            this.fileName = fileName;
            this.content = new LinkedList<>(content);
        }

        @Override
        public void change(State state) throws StateException {
            state.create(fileName, content);
        }
    }

    public static class Remove implements Diff {

        private final String fileName;

        public Remove(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void change(State state) throws StateException {
            state.remove(fileName);
        }
    }
}
