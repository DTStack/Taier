package com.dtstack.batch.enums;

/**
 * @author mading
 * @create 2019-03-18 11:40 AM
 */
public enum AlarmType {

    /**
     * 任务失败
     */
    TASK_FAIL("任务失败"),

    /**
     * 任务停止
     */
    TASK_STOP("任务停止"),

    /**
     * 定时未完成
     */
    TIMING_UNCOMPLETED("定时%s未完成"),

    /**
     * 超时未完成
     */
    TIMING_EXEC_OVER("超时,%s分钟未完成");

    String type;

    AlarmType(String type) {
        this.type = type;
    }

    public String getType() {
         return this.type;
    }
}
