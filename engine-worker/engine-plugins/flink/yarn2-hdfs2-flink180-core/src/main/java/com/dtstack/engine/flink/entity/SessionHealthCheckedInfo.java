package com.dtstack.engine.flink.entity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/9/11
 */
public class SessionHealthCheckedInfo {

    /**
     * session 是否健康运行
     */
    private volatile boolean running = true;

    private AtomicInteger submitErrorCount = new AtomicInteger(0);

    private volatile long lastResetTIme;

    public boolean isRunning() {
        return running;
    }

    public int getSubmitErrorCount() {
        return submitErrorCount.get();
    }

    public long getLastResetTIme() {
        return lastResetTIme;
    }

    public int incrSubmitError() {
        return submitErrorCount.incrementAndGet();
    }

    public void unHealth() {
        this.running = false;
    }

    public void reset() {
        this.running = true;
        this.lastResetTIme = System.currentTimeMillis();
        this.submitErrorCount.set(0);
    }

}
