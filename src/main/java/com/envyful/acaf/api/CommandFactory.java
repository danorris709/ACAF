package com.envyful.acaf.api;

public interface CommandFactory {

    boolean registerCommand(Object o);

    boolean unregisterCommand(Object o);

}
