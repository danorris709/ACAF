package com.envyful.acaf.impl.injector;

import com.envyful.acaf.api.injector.ArgumentInjector;
import net.minecraft.command.ICommandSender;

import java.util.function.BiFunction;

public class FunctionInjector<T> implements ArgumentInjector<T> {

    private final Class<T> superClass;
    private final boolean multipleArgs;
    private final BiFunction<ICommandSender, String[], T> function;

    public FunctionInjector(Class<T> superClass, boolean multipleArgs, BiFunction<ICommandSender, String[], T> function) {
        this.superClass = superClass;
        this.multipleArgs = multipleArgs;
        this.function = function;
    }

    @Override
    public Class<T> getSuperClass() {
        return this.superClass;
    }

    @Override
    public boolean doesRequireMultipleArgs() {
        return this.multipleArgs;
    }

    @Override
    public T instantiateClass(ICommandSender sender, String... args) {
        return this.function.apply(sender, args);
    }
}
