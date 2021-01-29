package com.envyful.acaf.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UtilConcurrency {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(6);

    public static void executeAsync(Runnable runnable) {
        THREAD_POOL.submit(runnable);
    }

}
