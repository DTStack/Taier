package com.dtstack.engine.master.task;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.logStore.LogStoreFactory;
import com.dtstack.engine.master.zk.listener.MasterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sishu.yss on 2018/2/26.
 */
public class LogStoreListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LogStoreListener.class);

    private final static int CHECK_INTERVAL = 18000000;

    private MasterListener masterListener;

    private static LogStoreListener listener;

    public static void init(MasterListener masterListener) {
        listener = new LogStoreListener(masterListener);
    }

    private LogStoreListener(MasterListener masterListener) {
        this.masterListener = masterListener;
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("HeartBeatCheckListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            logger.info("LogStoreListener start again...");
            if (masterListener.isMaster()) {
                LogStoreFactory.getLogStore(null).clearJob();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
