package com.envyful.acaf.api.injector;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ArgumentInjector<T> {

    Class<T> getSuperClass();

    T instantiateClass(EntityPlayerMP sender, String... arguments);

}
