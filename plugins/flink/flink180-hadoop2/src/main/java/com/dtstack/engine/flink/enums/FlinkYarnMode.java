package com.dtstack.engine.flink.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/29
 */
public enum FlinkYarnMode {

    //flink 1.5版本新模式，RestClusterClient
    NEW,

    //flink 每一个job就是一个flink cluster 任务，部署+提交
    PER_JOB;

    public static FlinkYarnMode mode(String mode) {
        if (FlinkYarnMode.NEW.name().equalsIgnoreCase(mode)) {
            return NEW;
        } else {
            return PER_JOB;
        }
    }

    public static boolean isPerJob(FlinkYarnMode mode) {
        if (mode != null && mode == PER_JOB) {
            return true;
        }
        return false;
    }

}
