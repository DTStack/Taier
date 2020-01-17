package com.dtstack.task.server.queue;

import com.dtstack.task.common.enums.SentinelType;
import com.dtstack.task.server.bo.ScheduleBatchJob;

import java.util.concurrent.atomic.AtomicLong;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/28
 */
public class BatchJobElement extends OrderObject {

    private final static AtomicLong PRIORITY_SUFFIX = new AtomicLong(1);
    private final static Long LIMITED = 100000L;
    /**
     * 设置哨兵，0:非哨兵，1：哨兵-最后的db数据，2：队列最后的数据
     */
    public SentinelType sentinel = SentinelType.NONE;

    private ScheduleBatchJob scheduleBatchJob;

    public BatchJobElement(SentinelType sentinelType) {
        this.sentinel = sentinelType;
        this.priority = Long.MAX_VALUE;
    }

    public BatchJobElement(ScheduleBatchJob scheduleBatchJob) {
        PRIORITY_SUFFIX.compareAndSet(LIMITED, 0);
        this.priority = Long.valueOf(scheduleBatchJob.getCycTime()) * LIMITED + PRIORITY_SUFFIX.incrementAndGet();
        this.scheduleBatchJob = scheduleBatchJob;
    }

    public ScheduleBatchJob getScheduleBatchJob() {
        return scheduleBatchJob;
    }

    String getJobId() {
        if (SentinelType.NONE != sentinel) {
            return "";
        }
        return scheduleBatchJob.getJobId();
    }

    public boolean isSentinel() {
        return sentinel.isSentinel();
    }

    public SentinelType getSentinel() {
        return sentinel;
    }
}
