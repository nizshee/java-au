package com.github.nizshee.node;


import com.github.nizshee.CommitMessage;
import com.github.nizshee.exception.StateException;
import com.github.nizshee.state.State;

import java.io.Serializable;
import java.util.List;

/**
 * Class that represents change of workspace.
 */
public interface Node extends Serializable {
    List<String> getDependencies();
    State change(List<State> states) throws StateException;
    CommitMessage message(String hash);
}
