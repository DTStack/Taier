package com.dtstack.task.runner;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;

/**
 * company: www.dtstack.com
 *
 * @author toutian
 *         create: 2019/10/22
 */
public class ShutDownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutDownHook.class);

    private List<Closeable> closeables = Lists.newArrayList();

    public ShutDownHook(List<Closeable> closeables) {
        closeables.addAll(closeables);
    }

    public void addShutDownHook() {
        Thread shut = new Thread(new ShutDownHookThread());
        shut.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(shut);
        logger.warn("addShutDownHook success ...");
    }

    private class ShutDownHookThread implements Runnable {

        @Override
        public void run() {
            logger.warn("dt-center-task shutdown now...");
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (Throwable e) {
                        logger.error("{}", e);
                    }
                }
            }
        }
    }
}
