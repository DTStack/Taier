package com.dtstack.taier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScriptExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptExecutor.class);

    /**
     * sql 执行默认核心线程数
     */
    private static final Integer DEFAULT_POOL_SIZE = 10;

    public static ThreadPoolExecutor sqlExecutor = null;

    public static ScheduledExecutorService dataCleanExecutor = null;

    public final static Map<String, ScriptJob> JOB_MAP = new ConcurrentHashMap<>();

    public static ThreadPoolExecutor getSqlExecutor() {
        return sqlExecutor;
    }

    public static Map<String, ScriptJob> getJobMap() {
        return JOB_MAP;
    }

    public static void buildExecutor() {
        if (sqlExecutor == null) {
            synchronized (ScriptExecutor.class) {
                if (sqlExecutor == null) {
                    sqlExecutor = new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE, 0, TimeUnit.MILLISECONDS,
                            new SynchronousQueue<>(true), new ScriptClassThreadFactory("script-run"));
                }
            }
        }

        if (dataCleanExecutor == null) {
            synchronized (ScriptExecutor.class) {
                if (dataCleanExecutor == null) {
                    dataCleanExecutor = new ScheduledThreadPoolExecutor(1, new ScriptClassThreadFactory("clean-script-cache"));
                    dataCleanExecutor.scheduleAtFixedRate(ScriptExecutor::cleanCache, 0, 2L, TimeUnit.MINUTES);
                }
            }
        }
    }

    public static void cleanCache() {
        for (Map.Entry<String, ScriptJob> jobEntry : getJobMap().entrySet()) {
            ScriptJob scriptJob = jobEntry.getValue();
            // 默认运行结束后 5 min 清理
            if (Objects.nonNull(scriptJob.getExecEndTime()) && System.currentTimeMillis() - scriptJob.getExecEndTime() > 5 * 60 * 1000) {
                LOGGER.info("clean expire job, jobId: {}", jobEntry.getKey());
                getJobMap().remove(jobEntry.getKey());
            }
        }
    }

}
