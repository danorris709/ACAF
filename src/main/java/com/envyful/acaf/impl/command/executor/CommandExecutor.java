package com.envyful.acaf.impl.command.executor;

import com.envyful.acaf.api.injector.ArgumentInjector;
import com.envyful.acaf.impl.command.SenderType;
import net.minecraft.command.ICommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandExecutor {

    private final String identifier;
    private final int senderPosition;
    private final SenderType sender;
    private final Object commandClass;
    private final Method executor;
    private final boolean executeAsync;
    private final int requiredArgs;
    private final String requiredPermission;
    private final ArgumentInjector<?>[] arguments;

    public CommandExecutor(String identifier, SenderType sender, int senderPosition, Object commandClass, Method executor,
                           boolean executeAsync, String requiredPermission, ArgumentInjector<?>[] arguments) {
        this.identifier = identifier;
        this.senderPosition = senderPosition;
        this.sender = sender;
        this.commandClass = commandClass;
        this.executor = executor;
        this.executeAsync = executeAsync;
        this.requiredPermission = requiredPermission;
        this.arguments = arguments;
        this.requiredArgs = this.calculateRequiredArgs();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public SenderType getSender() {
        return this.sender;
    }

    private int calculateRequiredArgs() {
        for (ArgumentInjector<?> argument : this.arguments) {
            if (argument.doesRequireMultipleArgs()) {
                return -1;
            }
        }

        return this.arguments.length;
    }

    public boolean canExecute(ICommandSender sender) {
        if (this.requiredPermission == null || this.requiredPermission.isEmpty()) {
            return true;
        }

        return sender.canUseCommand(4, this.requiredPermission);
    }

    public boolean isExecuteAsync() {
        return this.executeAsync;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    public boolean execute(ICommandSender sender, String[] arguments) {
        Object[] args = new Object[Math.max(1, arguments.length)];

        for (int i = 0; i < this.arguments.length; i++) {
            if (i == this.senderPosition) {
                continue;
            }

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

        args[this.senderPosition] = this.sender.getType().cast(sender);

        try {
            this.executor.invoke(this.commandClass, args);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }
}
