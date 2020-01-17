package com.dtstack.engine.master.alarm;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/30
 */
public enum AlarmType {

    TASK_FAIL("任务失败"),
    TASK_STOP("任务停止"),
    TIMING_UNCOMPLETED("定时%s未完成"),
    TIMING_EXEC_OVER("超时,%s分钟未完成");

    String type;

    AlarmType(String type) {
        this.type = type;
    }

    public String getType() {
         return this.type;
    }
}
