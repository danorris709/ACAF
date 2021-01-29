package com.envyful.acaf.api.exception;

public class CommandLoadException extends Exception {

    public CommandLoadException(String className, String reason) {
        super("Failed to load command " + className + " for reason: " + reason);
    }
}
