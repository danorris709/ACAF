package com.envyful.acaf.api;

import com.envyful.acaf.api.exception.CommandLoadException;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.function.BiFunction;

public interface CommandFactory {

    boolean registerCommand(Object o) throws CommandLoadException;

    boolean unregisterCommand(Object o);

    void registerInjector(Class<?> parentClass, BiFunction<EntityPlayerMP, String[], ?> function);

    void unregisterInjector(Class<?> parentClass);

}
