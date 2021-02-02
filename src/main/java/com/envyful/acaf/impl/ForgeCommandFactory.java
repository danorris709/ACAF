package com.envyful.acaf.impl;

import com.envyful.acaf.api.CommandFactory;
import com.envyful.acaf.api.command.Child;
import com.envyful.acaf.api.command.Command;
import com.envyful.acaf.api.command.Permissible;
import com.envyful.acaf.api.command.SubCommands;
import com.envyful.acaf.api.command.executor.CommandProcessor;
import com.envyful.acaf.api.command.executor.Sender;
import com.envyful.acaf.api.exception.CommandLoadException;
import com.envyful.acaf.api.injector.ArgumentInjector;
import com.envyful.acaf.impl.command.ForgeCommand;
import com.envyful.acaf.impl.command.SenderType;
import com.envyful.acaf.impl.command.executor.CommandExecutor;
import com.envyful.acaf.impl.injector.FunctionInjector;
import com.envyful.acaf.impl.thread.ServerTickListener;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ForgeCommandFactory implements CommandFactory {

    private final List<ArgumentInjector<?>> registeredInjectors = Lists.newArrayList();
    private final ServerTickListener tickListener = new ServerTickListener();

    public ForgeCommandFactory() {
        this.registerInjector(EntityPlayerMP.class, (sender, args) -> sender.getServer().getPlayerList().getPlayerByUsername(args[0]));

        MinecraftForge.EVENT_BUS.register(this.tickListener);
    }

    @Override
    public boolean registerCommand(MinecraftServer server, Object o) throws CommandLoadException {
        ForgeCommand command = this.createCommand(o.getClass(), o);
        ((CommandHandler) server.getCommandManager()).registerCommand(command);
        return true;
    }

    private ForgeCommand createCommand(Class<?> clazz) throws CommandLoadException {
        return this.createCommand(clazz, null);
    }

    private ForgeCommand createCommand(Class<?> clazz, Object instance) throws CommandLoadException {
        List<ForgeCommand> subCommands = this.getSubCommands(clazz);
        Command commandData = clazz.getAnnotation(Command.class);

        if (commandData == null) {
            throw new CommandLoadException(clazz.getSimpleName(), "missing @Command annotation on class!");
        }

        String defaultPermission = this.getDefaultPermission(clazz);

        if (clazz.getAnnotation(Child.class) != null) {
            throw new CommandLoadException(clazz.getSimpleName(), "cannot register child commands as a root command");
        }

        if (instance  == null) {
            instance = this.createInstance(clazz);

            if (instance == null) {
                throw new CommandLoadException(clazz.getSimpleName(), "cannot instantiate sub-command as there's no public constructor");
            }
        }

        List<CommandExecutor> subExecutors = Lists.newArrayList();

        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            CommandProcessor processorData = declaredMethod.getAnnotation(CommandProcessor.class);

            if (processorData == null) {
                continue;
            }

            String requiredPermission = this.getPermission(declaredMethod);
            List<ArgumentInjector<?>> arguments = Lists.newArrayList();
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            Annotation[][] annotations = declaredMethod.getParameterAnnotations();
            SenderType senderType = null;
            int senderPosition = -1;

            for (int i = 0; i < parameterTypes.length; i++) {
                if (annotations[i][0] instanceof Sender) {
                    senderType = SenderType.get(parameterTypes[i]);
                    senderPosition = i;
                } else {
                    arguments.add(this.getInjectorFor(parameterTypes[i]));
                }
            }

            if (senderType == null) {
                throw new CommandLoadException(clazz.getSimpleName(), "Command must have a sender!");
            }

            subExecutors.add(new CommandExecutor(processorData.value(), senderType, senderPosition, instance, declaredMethod,
                    processorData.executeAsync(), requiredPermission, arguments.toArray(new ArgumentInjector<?>[0])));
        }

        return new ForgeCommand(this, commandData.value(), defaultPermission, Arrays.asList(commandData.aliases()), subExecutors, subCommands);
    }

    private String getPermission(Method method) {
        Permissible permissible = method.getAnnotation(Permissible.class);

        if (permissible == null) {
            return "";
        }

        return permissible.value();
    }

    private Object createInstance(Class<?> clazz) {
        if (clazz.getConstructors().length == 0) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        for (Constructor<?> constructor : clazz.getConstructors()) {
            List<Object> objects = Lists.newArrayList();

            for (Class<?> parameterType : constructor.getParameterTypes()) {
                Object o = this.getInjectorFor(parameterType).instantiateClass(null);

                if (o == null) {
                    break;
                }

                objects.add(o);
            }

            if (objects.size() != constructor.getParameterTypes().length) {
                continue;
            }

            try {
                return constructor.newInstance(objects.toArray(new Object[0]));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getDefaultPermission(Class<?> clazz) {
        Permissible permissible = clazz.getAnnotation(Permissible.class);

        if (permissible == null) {
            return "";
        }

        return permissible.value();
    }

    private List<ForgeCommand> getSubCommands(Class<?> clazz) {
        SubCommands subCommands = clazz.getAnnotation(SubCommands.class);

        if (subCommands == null) {
            return Collections.emptyList();
        }

        List<ForgeCommand> commands = Lists.newArrayList();

        for (Class<?> subClazz : subCommands.value()) {
            commands.add(this.createCommand(subClazz));
        }

        return commands;
    }

    @Override
    public boolean unregisterCommand(MinecraftServer server, Object o) {
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void registerInjector(Class<?> parentClass, boolean multipleArgs, BiFunction<ICommandSender, String[], ?> function) {
        this.registeredInjectors.add(new FunctionInjector(parentClass, multipleArgs, function));
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

    public void executeSync(Runnable runnable) {
        this.tickListener.addTask(runnable);
    }
}
