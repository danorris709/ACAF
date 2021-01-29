package com.envyful.acaf.api.injector;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ArgumentInjector {

    Class<?> getSuperClass();

    Object instantiateClass(EntityPlayerMP sender, String... arguments);

}
