package com.envyful.acaf.impl.command;

import com.google.common.collect.Maps;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;

public enum SenderType {

    CONSOLE(ICommandSender.class),
    PLAYER(EntityPlayer.class),

    ;

    private static final Map<Class<?>, SenderType> SENDERS = Maps.newHashMap();

    static {
        for (SenderType value : values()) {
            SENDERS.put(value.clazz, value);
        }
    }

    private final Class<?> clazz;

    SenderType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static SenderType get(Class<?> clazz) {
        return SENDERS.get(clazz);
    }
}
