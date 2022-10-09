package com.dtstack.taier.datasource.api.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * manager thread factory
 *
 * @author ：wangchuan
 * date：Created in 14:49 2022/9/23
 * company: www.dtstack.com
 */
public class CustomThreadFactory implements ThreadFactory {

    private final static AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final static AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public CustomThreadFactory(String factoryName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = factoryName + "-pool-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + THREAD_NUMBER.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
