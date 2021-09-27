package com.dtstack.engine.master.jobdealer.bo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 停止的任务
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class StoppedJob<T> implements Delayed {
    private T job;
    private int count;
    private int retry;
    private long now;
    private long expired;

    public StoppedJob(T job, int retry, long delay) {
        this.job = job;
        this.retry = retry;
        this.now = System.currentTimeMillis();
        this.expired = now + delay;
    }

    public void incrCount() {
        count += 1;
    }

    public int getIncrCount() {
        return count;
    }

    public boolean isRetry() {
        return retry == 0 || count <= retry;
    }

    public void resetDelay(long delay) {
        this.now = System.currentTimeMillis();
        this.expired = now + delay;
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