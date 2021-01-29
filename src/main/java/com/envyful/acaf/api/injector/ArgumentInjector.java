package com.envyful.acaf.api.injector;

import net.minecraft.command.ICommandSender;

public interface ArgumentInjector<T> {

    Class<T> getSuperClass();

    boolean doesRequireMultipleArgs();

    T instantiateClass(ICommandSender sender, String... arguments);

}
