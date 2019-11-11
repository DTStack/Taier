package com.dtstack.engine.flink180.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/29
 */
public enum FlinkYarnMode {

    //flink 1.5版本新模式，RestClusterClient
    NEW,

    //兼容flink 1.4版本遗留下来的执行模式
    LEGACY,

    //flink 每一个job就是一个flink cluster 任务，部署+提交
    PER_JOB;

    public static FlinkYarnMode mode(String mode) {
        if (FlinkYarnMode.NEW.name().equalsIgnoreCase(mode)) {
            return NEW;
        } else if (FlinkYarnMode.PER_JOB.name().equalsIgnoreCase(mode)) {
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
