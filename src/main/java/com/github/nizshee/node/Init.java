package com.github.nizshee.node;


import com.github.nizshee.message.CommitMessage;
import com.github.nizshee.state.State;

import java.util.Collections;
import java.util.List;

public class Init implements Node {

    @Override
    public List<String> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public State change(List<State> states) {
        return new State();
    }

    @Override
    public CommitMessage message(String hash) {
        return new CommitMessage("init", hash);
    }
}
