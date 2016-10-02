package com.github.nizshee.index;


import com.github.nizshee.exception.StateException;
import com.github.nizshee.node.Node;
import com.github.nizshee.state.State;

/**
 * Class to store {@code Node}.
 */
public interface Index {

    String addNode(Node node);

    Node getNode(String hash) throws StateException;

    State getState(String hash) throws StateException;
}
