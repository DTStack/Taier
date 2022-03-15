package com.dtstack.taier.flink.session.check;


import com.dtstack.taier.flink.base.enums.SessionState;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @program: engine-plugins
 * @author: lany
 * @create: 2021/07/11 21:26
 */
public class SessionHealthInfo {

    /**
     * session 是否健康运行
     */
    private volatile SessionState sessionState = SessionState.UNHEALTHY;

    private AtomicInteger submitErrorCount = new AtomicInteger(0);

    private volatile long lastResetTIme;

    public boolean getSessionState() {
        return sessionState.getState();
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

    public void unHealthy() {
        this.sessionState = SessionState.UNHEALTHY;
    }

    public void healthy() {
        this.sessionState = SessionState.HEALTHY;
        this.lastResetTIme = System.currentTimeMillis();
        this.submitErrorCount.set(0);
    }

}

