package com.github.nizshee.index;


import com.github.nizshee.exception.StateException;
import com.github.nizshee.node.Node;
import com.github.nizshee.state.State;

import java.io.Serializable;
import java.util.*;

public class InMemoryIndex implements Index, Serializable {

    private final Map<String, Node> nodes;

    public InMemoryIndex(Map<String, Node> nodes) {
        this.nodes = new HashMap<>(nodes);
    }

    @Override
    public String addNode(Node node) {
        String hash = UUID.randomUUID().toString();
        nodes.put(hash, node);
        return hash;
    }

    @Override
    public Node getNode(String hash) throws StateException {
        if (!nodes.containsKey(hash)) throw new StateException("Key " + hash + " not found.");
        return nodes.get(hash);
    }

    @Override
    public State getState(String hash) throws StateException {
        Node node = getNode(hash);
        List<String> dependencies = node.getDependencies();
        List<State> states = new LinkedList<>();
        for (String dependencyHash: dependencies) {
            State state = getState(dependencyHash);
            states.add(state);
        }
        return node.change(states);
    }
}
