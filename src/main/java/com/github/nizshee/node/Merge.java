package com.github.nizshee.node;


import com.github.nizshee.CommitMessage;
import com.github.nizshee.exception.StateException;
import com.github.nizshee.state.State;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Merge implements Node {

    private final String from;
    private final String with;

    public Merge(String from, String with) {
        this.from = from;
        this.with = with;
    }

    @Override
    public List<String> getDependencies() {
        List<String> list = new LinkedList<>();
        list.add(from);
        list.add(with);
        return list;
    }

    @Override
    public State change(List<State> states) throws StateException {
        if (states.size() != 2) throw new StateException("Merge need two states");
        State from = states.get(0);
        State with = states.get(1);

        Set<String> toAdd = from.created(with);

        for (String fileName: toAdd) {
            from.create(fileName, with.get(fileName));
        }

        Set<String> toChange = with.changed(from);

        for (String fileName: toChange) {
            List<String> result = new LinkedList<>();
            result.add("<<<");
            result.addAll(from.get(fileName));
            result.add("===");
            result.addAll(with.get(fileName));
            result.add(">>>");
            from.put(fileName, result);
        }

        return from;
    }

    @Override
    public CommitMessage message(String hash) {
        return new CommitMessage(from + " with " + with, hash);
    }
}
