package com.github.nizshee.shared.exception;


public class ConnectionClosedException extends Exception {
    public ConnectionClosedException() {
        super("Connection closed.");
    }
}
