package com.dtstack.engine.flink.entity;

import java.util.concurrent.atomic.AtomicLong;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/9/11
 */
public class SessionCheckInterval {

    public int checkSubmitJobGraphInterval;
    public AtomicLong checkSubmitJobGraph = new AtomicLong(0);

    public SessionHealthCheckedInfo sessionHealthCheckedInfo;

    public SessionCheckInterval(int checkSubmitJobGraphInterval, SessionHealthCheckedInfo sessionHealthCheckedInfo) {
        this.checkSubmitJobGraphInterval = checkSubmitJobGraphInterval;
        this.sessionHealthCheckedInfo = sessionHealthCheckedInfo;
    }

    /**
     * 1: 是否开启了check
     * 2: 是否满足interval条件
     * 3: submit error在interval时间内超过了指定次数
     *
     * @return
     */
    public boolean doCheck() {
        boolean checkRs = checkSubmitJobGraphInterval > 0 && (!sessionHealthCheckedInfo.isRunning()
                || checkSubmitJobGraph.getAndIncrement() % checkSubmitJobGraphInterval == 0
                || sessionHealthCheckedInfo.getSubmitErrorCount() >= 3);
        if (checkRs) {
            sessionHealthCheckedInfo.reset();
        }
        return checkRs;
    }
}