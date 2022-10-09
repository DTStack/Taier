package com.dtstack.taier.datasource.plugin.common;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:20 2020/11/9
 * @Description：线程池工厂
 */
public class DtClassThreadFactory implements ThreadFactory {
    private final static AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final static AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public DtClassThreadFactory(String factoryName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = factoryName + "-pool-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = new Thread(group, r, namePrefix + THREAD_NUMBER.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
