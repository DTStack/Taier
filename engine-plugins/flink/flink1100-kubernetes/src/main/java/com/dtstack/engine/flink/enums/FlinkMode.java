package com.dtstack.engine.flink.enums;

import com.dtstack.engine.common.exception.RdosException;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/04/03
 */
public enum FlinkMode {

    //session模式
    SESSION,

    //flink 每一个job就是一个flink cluster 任务，部署+提交
    PER_JOB;

    public static FlinkMode mode(String mode) {
        if (FlinkMode.SESSION.name().equalsIgnoreCase(mode)) {
            return SESSION;
        } else if (FlinkMode.PER_JOB.name().equalsIgnoreCase(mode)){
            return PER_JOB;
        }

        throw new RdosException("not support mode: " + mode);
    }

    public static boolean isPerJob(FlinkMode mode) {
        if (mode != null && mode == PER_JOB) {
            return true;
        }
        return false;
    }

}
