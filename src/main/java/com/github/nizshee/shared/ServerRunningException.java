package com.github.nizshee.shared;


public class ServerRunningException extends Exception {
    public ServerRunningException() {
        super("Server already running.");
    }
}
