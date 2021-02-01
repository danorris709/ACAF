package com.envyful.acaf.impl.thread;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class ServerTickListener {

    private final List<Runnable> tasks = Lists.newArrayList();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Runnable task : this.tasks) {
            task.run();
        }
    }

    public void addTask(Runnable runnable) {
        this.tasks.add(runnable);
    }
}
