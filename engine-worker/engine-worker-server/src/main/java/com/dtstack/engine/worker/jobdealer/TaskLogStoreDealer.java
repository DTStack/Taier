package com.dtstack.engine.worker.jobdealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.logstore.AbstractLogStore;
import com.dtstack.engine.common.logstore.LogStoreFactory;
import com.dtstack.engine.worker.env.WorkerEnvironmentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sishu.yss on 2018/2/26.
 */
@Component
public class TaskLogStoreDealer implements Runnable, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLogStoreDealer.class);

    private final static int CHECK_INTERVAL = 18000000;

    private AbstractLogStore logStore;
    private ScheduledExecutorService scheduledService;
    private Map<String, String> dbConfig = new HashMap<>(3);

    @Autowired
    private WorkerEnvironmentContext workerEnvironmentContext;

    @Override
    public void run() {
        try {
            LOGGER.info("TaskLogStoreDealer start again...");
            logStore.clearJob();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dbConfig.put(ConfigConstant.JDBCURL, workerEnvironmentContext.getWorkerLogstoreJdbcUrl());
        dbConfig.put(ConfigConstant.USERNAME, workerEnvironmentContext.getWorkerLogstoreUsername());
        dbConfig.put(ConfigConstant.PASSWORD, workerEnvironmentContext.getWorkerLogstorePassword());
        dbConfig.put(ConfigConstant.INITIAL_SIZE, workerEnvironmentContext.getWorkerInitialSize());
        dbConfig.put(ConfigConstant.MINIDLE, workerEnvironmentContext.getWorkerMinActive());
        dbConfig.put(ConfigConstant.MAXACTIVE, workerEnvironmentContext.getWorkerMaxActive());

        logStore = LogStoreFactory.getLogStore(dbConfig);

        this.scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }
}
