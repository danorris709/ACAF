package com.envyful.acaf.api;

import com.envyful.acaf.api.exception.CommandLoadException;

import java.util.function.Supplier;

public interface CommandFactory {

    boolean registerCommand(Object o) throws CommandLoadException;

    boolean unregisterCommand(Object o);

    void registerInjector(Class<?> parentClass, Supplier<?> supplier);

    void unregisterInjector(Class<?> parentClass);

}
