package com.envyful.acaf.api;

import com.envyful.acaf.api.exception.CommandLoadException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.function.BiFunction;

public interface CommandFactory {

    boolean registerCommand(MinecraftServer server, Object o) throws CommandLoadException;

    boolean unregisterCommand(MinecraftServer server, Object o);

    default void registerInjector(Class<?> parentClass, BiFunction<ICommandSender, String[], ?> function) {
        this.registerInjector(parentClass, false, function);
    }

    void registerInjector(Class<?> parentClass, boolean multipleArgs, BiFunction<ICommandSender, String[], ?> function);

    void unregisterInjector(Class<?> parentClass);

}
