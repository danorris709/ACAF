package com.envyful.acaf.impl.injector;

import com.envyful.acaf.api.injector.ArgumentInjector;
import net.minecraft.command.ICommandSender;

import java.util.function.BiFunction;

public class FunctionInjector<T> implements ArgumentInjector<T> {

    private final Class<T> superClass;
    private final BiFunction<ICommandSender, String[], T> function;

    public FunctionInjector(Class<T> superClass, BiFunction<ICommandSender, String[], T> function) {
        this.superClass = superClass;
        this.function = function;
    }

    @Override
    public Class<T> getSuperClass() {
        return this.superClass;
    }

    @Override
    public T instantiateClass(ICommandSender sender, String... args) {
        return this.function.apply(sender, args);
    }
}
