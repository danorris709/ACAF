package com.envyful.acaf.impl.command.executor;

import com.envyful.acaf.api.injector.ArgumentInjector;
import net.minecraft.command.ICommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandExecutor {

    private final Object commandClass;
    private final Method executor;
    private final boolean executeAsync;
    private final int requiredArgs;
    private final ArgumentInjector<?>[] arguments;

    public CommandExecutor(Object commandClass, Method executor, boolean executeAsync, ArgumentInjector<?>[] arguments) {
        this.commandClass = commandClass;
        this.executor = executor;
        this.executeAsync = executeAsync;
        this.arguments = arguments;
        this.requiredArgs = this.calculateRequiredArgs();
    }

    private int calculateRequiredArgs() {
        for (ArgumentInjector<?> argument : this.arguments) {
            if (argument.doesRequireMultipleArgs()) {
                return -1;
            }
        }

        return this.arguments.length;
    }

    public boolean isExecuteAsync() {
        return this.executeAsync;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    public boolean execute(ICommandSender sender, String[] arguments) {
        Object[] args = new Object[this.arguments.length];

        for (int i = 0; i < this.arguments.length; i++) {
            ArgumentInjector<?> argument = this.arguments[i];

            if (argument.doesRequireMultipleArgs()) {
                String[] remainingArgs = Arrays.copyOfRange(arguments, i, arguments.length);

                args[i] = argument.instantiateClass(sender, remainingArgs);

                if (args[i] == null) {
                    return false;
                }
            } else {
                args[i] = argument.instantiateClass(sender, arguments[i]);

                if (args[i] == null) {
                    return false;
                }
            }
        }

        try {
            this.executor.invoke(this.commandClass, args);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }
}
