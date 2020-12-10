package com.dtstack.engine.flink.enums;

import com.dtstack.engine.common.exception.RdosDefineException;

/**
 * Created by sishu.yss on 2018/3/9.
 */
public enum ClusterMode {
    //yarn session模式
    SESSION,

    //flink 每一个job就是一个flink cluster 任务，部署+提交
    PER_JOB,

    STANDALONE;

    public static ClusterMode getClusteMode(String clusterMode) {
        if (SESSION.name().equalsIgnoreCase(clusterMode)) {
            return SESSION;
        } else if (PER_JOB.name().equalsIgnoreCase(clusterMode) || PER_JOB.name().replace("_", "").equalsIgnoreCase(clusterMode)) {
            return PER_JOB;
        } else if (STANDALONE.name().equalsIgnoreCase(clusterMode)) {
            return STANDALONE;
        }

        throw new RdosDefineException("not support clusterMode: " + clusterMode);
    }
}
