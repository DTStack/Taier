package com.dtstack.engine.master.taskDealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.logStore.LogStoreFactory;
import com.dtstack.engine.master.listener.MasterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sishu.yss on 2018/2/26.
 */
public class TaskLogStoreDealer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskLogStoreDealer.class);

    private final static int CHECK_INTERVAL = 18000000;

    private MasterListener masterListener;

    private static TaskLogStoreDealer listener;

    public static void init(MasterListener masterListener) {
        listener = new TaskLogStoreDealer(masterListener);
    }

    private TaskLogStoreDealer(MasterListener masterListener) {
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
            logger.info("TaskLogStoreDealer start again...");
            if (masterListener.isMaster()) {
                LogStoreFactory.getLogStore(null).clearJob();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
