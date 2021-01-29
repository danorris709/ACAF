package com.envyful.acaf.api.injector;

import net.minecraft.command.ICommandSender;

public interface ArgumentInjector<T> {

    Class<T> getSuperClass();

    T instantiateClass(ICommandSender sender, String... arguments);

}
