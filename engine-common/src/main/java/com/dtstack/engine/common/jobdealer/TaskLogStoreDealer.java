package com.dtstack.engine.common.jobdealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.logstore.AbstractLogStore;
import com.dtstack.engine.common.logstore.LogStoreFactory;
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
public class TaskLogStoreDealer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskLogStoreDealer.class);

    private final static int CHECK_INTERVAL = 18000000;

    private AbstractLogStore logStore;
    private ScheduledExecutorService scheduledService;
    private Map<String, String> dbConfig = new HashMap<>(3);

    public static TaskLogStoreDealer instance = new TaskLogStoreDealer();

    public static TaskLogStoreDealer getInstance() {
        return instance;
    }

    public TaskLogStoreDealer() {
        dbConfig.put("url", AkkaConfig.getWorkerLogstoreJdbcUrl());
        dbConfig.put("userName", AkkaConfig.getWorkerLogstoreUsername());
        dbConfig.put("pwd", AkkaConfig.getWorkerLogstorePassword());
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
            logStore.clearJob();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
