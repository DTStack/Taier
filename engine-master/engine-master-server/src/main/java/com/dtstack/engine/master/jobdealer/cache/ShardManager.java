package com.dtstack.engine.master.jobdealer.cache;

import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * data 数据分片及空闲检测
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ShardManager implements Runnable {

    private static final long DATA_CLEAN_INTERVAL = 1000;
    private ScheduledExecutorService scheduledService = null;
    private Map<String, Integer> shard;
    private String jobResource;

    public ShardManager(String jobResource) {
        this.jobResource = jobResource;
        this.shard = new ConcurrentHashMap<>();
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(jobResource + this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                DATA_CLEAN_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public Integer putJob(String jobId, Integer status) {
        return shard.put(jobId, status);
    }

    public Integer removeJob(String jobId) {
        return shard.remove(jobId);
    }

    public Map<String, Integer> getShard() {
        return shard;
    }

    public String getJobResource() {
        return jobResource;
    }

    @Override
    public void run() {
        shard.entrySet().removeIf(jobWithStatus -> RdosTaskStatus.needClean(jobWithStatus.getValue()));
    }

}
