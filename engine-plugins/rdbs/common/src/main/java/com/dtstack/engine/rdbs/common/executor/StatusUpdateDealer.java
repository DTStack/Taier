package com.dtstack.engine.rdbs.common.executor;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.logstore.LogStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时更新任务的执行修改时间和清理过期任务
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * author: toutian
 */

public class StatusUpdateDealer {

    private static final Logger LOG = LoggerFactory.getLogger(StatusUpdateDealer.class);

    private final static int MODIFY_CHECK_INTERVAL = 2 * 1000;
    private final static int TIMEOUT_CHECK_INTERVAL = 30 * 1000;

    private Map<String, JobClient> jobCache;
    private ModifyCheckJob modifyCheckJob;
    private TimeoutCheckJob timeoutCheckJob;

    private ScheduledExecutorService scheduledService;

    public StatusUpdateDealer(Map<String, JobClient> jobCache) {
        this.jobCache = jobCache;
        modifyCheckJob = new ModifyCheckJob();
        timeoutCheckJob = new TimeoutCheckJob();
        scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory(this.getClass().getSimpleName()));
    }

    public void start() {
        scheduledService.scheduleWithFixedDelay(
                modifyCheckJob,
                MODIFY_CHECK_INTERVAL,
                MODIFY_CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
        scheduledService.scheduleWithFixedDelay(
                timeoutCheckJob,
                TIMEOUT_CHECK_INTERVAL,
                TIMEOUT_CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    private class ModifyCheckJob implements Runnable {
        @Override
        public void run() {
            try {
                LogStoreFactory.getLogStore().updateModifyTime(jobCache.keySet());
            } catch (Throwable e) {
                LOG.error("", e);
            }
        }
    }

    private class TimeoutCheckJob implements Runnable {
        @Override
        public void run() {
            try {
                LogStoreFactory.getLogStore().timeOutDeal();
            } catch (Throwable e) {
                LOG.error("", e);
            }
        }
    }
}
