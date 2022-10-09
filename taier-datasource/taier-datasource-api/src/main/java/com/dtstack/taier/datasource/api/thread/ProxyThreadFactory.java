package com.dtstack.taier.datasource.api.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提供代理使用的 thread factory, 每个 classloader 维护一份, 用于设置线程上下文 classloader
 *
 * @author ：wangchuan
 * date：Created in 14:49 2022/9/23
 * company: www.dtstack.com
 */
public class ProxyThreadFactory implements ThreadFactory {

    private final static AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final static AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final ClassLoader classLoader;
    private final ThreadGroup group;
    private final String namePrefix;

    public ProxyThreadFactory(ClassLoader classLoader, String pluginName) {
        this.classLoader = classLoader;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = pluginName + "-proxy-pool-" +
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
        if (classLoader != null) {
            t.setContextClassLoader(classLoader);
        }
        return t;
    }
}
