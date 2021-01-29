package com.envyful.acaf.impl;

import com.envyful.acaf.api.CommandFactory;
import com.envyful.acaf.api.command.Child;
import com.envyful.acaf.api.command.Command;
import com.envyful.acaf.api.command.Permissible;
import com.envyful.acaf.api.command.SubCommands;
import com.envyful.acaf.api.exception.CommandLoadException;
import com.envyful.acaf.api.injector.ArgumentInjector;
import com.envyful.acaf.impl.injector.FunctionInjector;
import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ForgeCommandFactory implements CommandFactory {

    private final List<ArgumentInjector<?>> registeredInjectors = Lists.newArrayList();

    @Override
    public boolean registerCommand(Object o) throws CommandLoadException {
        Class<?> clazz = o.getClass();
        Command command = clazz.getAnnotation(Command.class);

        if (command == null) {
            throw new CommandLoadException(clazz.getSimpleName(), "missing @Command annotation on class!");
        }

        String defaultPermission = this.getDefaultPermission(clazz);

        if (clazz.getAnnotation(Child.class) != null) {
            throw new CommandLoadException(clazz.getSimpleName(), "cannot register child commands as a root command");
        }

        Class<?>[] subCommands = this.getSubCommands(clazz);

        return false;
    }

    private String getDefaultPermission(Class<?> clazz) {
        Permissible permissible = clazz.getAnnotation(Permissible.class);

        if (permissible == null) {
            return "";
        }

        return permissible.value();
    }

    private Class<?>[] getSubCommands(Class<?> clazz) {
        SubCommands subCommands = clazz.getAnnotation(SubCommands.class);

        if (subCommands == null) {
            return new Class<?>[0];
        }

        return subCommands.value();
    }

    @Override
    public boolean unregisterCommand(Object o) {
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void registerInjector(Class<?> parentClass, BiFunction<ICommandSender, String[], ?> function) {
        this.registeredInjectors.add(new FunctionInjector(parentClass, function));
    }

    @Override
    public void unregisterInjector(Class<?> parentClass) {
        this.registeredInjectors.removeIf(next -> Objects.equals(parentClass, next.getSuperClass()));
    }

    public ArgumentInjector<?> getInjectorFor(Class<?> clazz) {
        for (ArgumentInjector<?> registeredInjector : this.registeredInjectors) {
            if (registeredInjector.getSuperClass().isAssignableFrom(clazz)) {
                return registeredInjector;
            }
        }

        return null;
    }
}
