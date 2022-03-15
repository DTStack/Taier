package com.dtstack.taier.flink.session.check;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @description:
 * @program: engine-plugins
 * @author: lany
 * @create: 2021/07/11 21:26
 */
public class SessionCheckInfo {

    /**
     * interval of check.
     * unit: second
     */
    public int checkSubmitJobGraphInterval;


    public AtomicLong checkSubmitJobGraph = new AtomicLong(0);

    /**
     * health info of session.
     */
    public SessionHealthInfo sessionHealthInfo;

    public SessionCheckInfo(int checkSubmitJobGraphInterval, SessionHealthInfo sessionHealthInfo) {
        this.checkSubmitJobGraphInterval = checkSubmitJobGraphInterval;
        this.sessionHealthInfo = sessionHealthInfo;
    }

    /**
     * 1: 是否开启了check
     * 2: 是否满足interval条件
     * 3: submit error在interval时间内超过了指定次数
     *
     * @return
     */
    public boolean doCheck() {
        boolean checkRs = checkSubmitJobGraphInterval > 0 && (!sessionHealthInfo.getSessionState()
                || checkSubmitJobGraph.getAndIncrement() % checkSubmitJobGraphInterval == 0
                || sessionHealthInfo.getSubmitErrorCount() >= 3);
        return checkRs;
    }

}
