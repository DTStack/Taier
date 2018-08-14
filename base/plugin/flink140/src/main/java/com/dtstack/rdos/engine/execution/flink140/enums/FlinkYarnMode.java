package com.dtstack.rdos.engine.execution.flink140.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/8/14
 */
public enum FlinkYarnMode {

    LEGACY,

    //flink 每一个job就是一个flink cluster 任务，部署+提交
    PER_JOB;

    public static FlinkYarnMode mode(String mode) {
        if (FlinkYarnMode.PER_JOB.name().equalsIgnoreCase(mode)) {
            return PER_JOB;
        } else {
            return LEGACY;
        }
    }

    public static boolean isLegacy(FlinkYarnMode mode) {
        if (mode != null && mode == LEGACY) {
            return true;
        }
        return false;
    }

    public static boolean isPerJob(FlinkYarnMode mode) {
        if (mode != null && mode == PER_JOB) {
            return true;
        }
        return false;
    }
}
