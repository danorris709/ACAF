package com.envyful.acaf.api;

import com.envyful.acaf.api.exception.CommandLoadException;

public interface CommandFactory {

    boolean registerCommand(Object o) throws CommandLoadException;

    boolean unregisterCommand(Object o);

}
