package com.dtstack.engine.common;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class BlockCallerPolicy implements RejectedExecutionHandler {

    public BlockCallerPolicy() {
    }

    /**
     * block policy handler
     *
     * @param r
     * @param executor
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException var4) {
            throw new RejectedExecutionException("Unexpected InterruptedException", var4);
        }
    }
}