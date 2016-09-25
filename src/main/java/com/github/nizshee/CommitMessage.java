package com.github.nizshee;


public class CommitMessage {
    public final String message;
    public final String hash;

    public CommitMessage(String message, String hash) {
        this.message = message;
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CommitMessage &&
                message.equals(((CommitMessage) o).message) && hash.equals(((CommitMessage) o).hash);
    }

    @Override
    public int hashCode() {
        return message.hashCode() + hash.hashCode();
    }

    @Override
    public String toString() {
        return "(" + hash + ", " + message + ")";
    }
}
