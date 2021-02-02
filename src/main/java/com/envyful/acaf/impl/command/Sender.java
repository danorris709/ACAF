package com.envyful.acaf.impl.command;

import com.google.common.collect.Maps;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;

public enum Sender {

    CONSOLE(ICommandSender.class),
    PLAYER(EntityPlayer.class),

    ;

    private static final Map<Class<?>, Sender> SENDERS = Maps.newHashMap();

    static {
        for (Sender value : values()) {
            SENDERS.put(value.clazz, value);
        }
    }

    private final Class<?> clazz;

    Sender(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Sender get(Class<?> clazz) {
        return SENDERS.get(clazz);
    }
}
