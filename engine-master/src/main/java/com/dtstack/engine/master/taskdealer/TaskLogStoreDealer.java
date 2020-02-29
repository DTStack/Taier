package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.logstore.AbstractLogStore;
import com.dtstack.engine.common.logstore.LogStoreFactory;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.listener.Listener;
import com.dtstack.engine.master.listener.MasterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sishu.yss on 2018/2/26.
 */
public class TaskLogStoreDealer implements Listener, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskLogStoreDealer.class);

    private final static int CHECK_INTERVAL = 18000000;

    private MasterListener masterListener;
    private AbstractLogStore logStore;
    private ScheduledExecutorService scheduledService;
    private Map<String, String> dbConfig = new HashMap<>(3);

    public TaskLogStoreDealer(MasterListener masterListener, EnvironmentContext environmentContext) {
        this.masterListener = masterListener;
        dbConfig.put("url", environmentContext.getJdbcUrl());
        dbConfig.put("userName", environmentContext.getJdbcUser());
        dbConfig.put("pwd", environmentContext.getJdbcPassword());
        logStore = LogStoreFactory.getLogStore(dbConfig);

        this.scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
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
                logStore.clearJob();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    public void close() throws Exception {
        scheduledService.shutdownNow();
    }
}
