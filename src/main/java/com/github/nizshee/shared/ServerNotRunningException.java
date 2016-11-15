package com.github.nizshee.shared;


public class ServerNotRunningException extends Exception {
    public ServerNotRunningException() {
        super("Server not running.");
    }
}
