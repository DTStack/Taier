package com.dtstack.engine.flink.enums;

/**
 * Created by sishu.yss on 2018/3/9.
 */
public enum ClusterMode {
    //yarn session模式
    SESSION,

    //flink 每一个job就是一个flink cluster 任务，部署+提交
    PER_JOB,

    STANDALONE;
}
