package com.dtstack.engine.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2020/07/01
 */
public class CustomThreadRunsPolicy implements RejectedExecutionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(CustomThreadRunsPolicy.class);

    private String threadName;

    private String type;

    private int timeout = 60;

    public CustomThreadRunsPolicy(String threadName, String type) {
        this.threadName = threadName;
        this.type = type;
    }

    public CustomThreadRunsPolicy(String threadName, String type, int timeout) {
        this.threadName = threadName;
        this.type = type;
        this.timeout = timeout;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread pool is EXHAUSTED!" +
                        " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in type:%s!",
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating(),
                type);
        try {
            logger.warn(msg);
            e.getQueue().offer(r, timeout, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
            logger.error(msg);
            throw new RejectedExecutionException("Interrupted waiting for worker");
        }
    }
}