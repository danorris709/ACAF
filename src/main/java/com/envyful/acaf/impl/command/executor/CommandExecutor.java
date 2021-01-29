package com.envyful.acaf.impl.command.executor;

import com.envyful.acaf.api.injector.ArgumentInjector;
import com.envyful.acaf.util.UtilConcurrency;
import net.minecraft.command.ICommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandExecutor {

    private final Object commandClass;
    private final Method executor;
    private final boolean executeAsync;
    private final ArgumentInjector<?>[] arguments;

    public CommandExecutor(Object commandClass, Method executor, boolean executeAsync, ArgumentInjector<?>[] arguments) {
        this.commandClass = commandClass;
        this.executor = executor;
        this.executeAsync = executeAsync;
        this.arguments = arguments;
    }

    public void execute(ICommandSender sender, String[] args) {
        if (!this.executeAsync) {
            this.runSync(sender, args);
            return;
        }

        UtilConcurrency.executeAsync(() -> this.runSync(sender, args));
    }

    private void runSync(ICommandSender sender, String[] arguments) {
        Object[] args = new Object[this.arguments.length];

        for (int i = 0; i < this.arguments.length; i++) {
            ArgumentInjector<?> argument = this.arguments[i];

            if (argument.doesRequireMultipleArgs()) {
                String[] remainingArgs = Arrays.copyOfRange(arguments, i, arguments.length);

                args[i] = argument.instantiateClass(sender, remainingArgs);
            } else {
                args[i] = argument.instantiateClass(sender, arguments[i]);
            }
        }

        try {
            this.executor.invoke(this.commandClass, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
