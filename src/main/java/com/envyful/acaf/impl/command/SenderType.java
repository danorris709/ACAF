package com.envyful.acaf.impl.command;

import com.google.common.collect.Maps;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Map;

public enum SenderType {

    CONSOLE(ICommandSender.class),
    PLAYER(EntityPlayer.class, EntityPlayerMP.class),

    ;

    private static final Map<Class<?>, SenderType> SENDERS = Maps.newHashMap();

    static {
        for (SenderType value : values()) {
            for (Class<?> clazz : value.clazz) {
                SENDERS.put(clazz, value);
            }
        }
    }

    private final Class<?>[] clazz;

    SenderType(Class<?>... clazz) {
        this.clazz = clazz;
    }

    public Class<?> getType() {
        return this.clazz[0];
    }

    public static SenderType get(Class<?> clazz) {
        return SENDERS.get(clazz);
    }
}
