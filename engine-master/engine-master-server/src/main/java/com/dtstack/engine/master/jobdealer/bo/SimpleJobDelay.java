package com.dtstack.engine.master.jobdealer.bo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 重试的任务
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class SimpleJobDelay<T> implements Delayed {
    private T job;
    private int stage;
    private long expired;

    public SimpleJobDelay(T job, int stage, long delay) {
        this.job = job;
        this.stage = stage;
        this.expired = System.currentTimeMillis() + delay;
    }

    public T getJob() {
        return job;
    }

    public int getStage() {
        return stage;
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