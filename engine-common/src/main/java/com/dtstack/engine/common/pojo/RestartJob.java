package com.dtstack.engine.common.pojo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 重试的任务
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class RestartJob<T> implements Delayed {
    private T job;
    private long expired;

    public RestartJob(T job, long delay) {
        this.job = job;
        this.expired = System.currentTimeMillis() + delay;
    }

    public T getJob() {
        return job;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}