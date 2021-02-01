package com.envyful.acaf.impl.injector.impl;

import com.envyful.acaf.impl.injector.FunctionInjector;
import net.minecraft.entity.player.EntityPlayerMP;

public class ForgePlayerInjector extends FunctionInjector<EntityPlayerMP> {

    public ForgePlayerInjector() {
        super(EntityPlayerMP.class,
                false,
                (iCommandSender, args) -> iCommandSender.getServer().getPlayerList().getPlayerByUsername(args[0]));
    }
}
