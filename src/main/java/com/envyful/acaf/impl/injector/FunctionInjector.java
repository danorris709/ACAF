package com.envyful.acaf.impl.injector;

import com.envyful.acaf.api.injector.ArgumentInjector;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.function.BiFunction;

public class FunctionInjector<T> implements ArgumentInjector<T> {

    private final Class<T> superClass;
    private final BiFunction<EntityPlayerMP, String[], T> function;

    public FunctionInjector(Class<T> superClass, BiFunction<EntityPlayerMP, String[], T> function) {
        this.superClass = superClass;
        this.function = function;
    }

    @Override
    public Class<T> getSuperClass() {
        return this.superClass;
    }

    @Override
    public T instantiateClass(EntityPlayerMP sender, String... args) {
        return this.function.apply(sender, args);
    }
}
