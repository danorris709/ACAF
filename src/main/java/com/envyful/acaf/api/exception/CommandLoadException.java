package com.envyful.acaf.api.exception;

public class CommandLoadException extends RuntimeException {

    public CommandLoadException(String className, String reason) {
        super("Failed to load command " + className + " for reason: " + reason);
    }
}
