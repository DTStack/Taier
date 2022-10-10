package com.dtstack.taier.datasource.api.enums;

/**
 * job相关状态统一维护，有的状态重复但是写法不同的，统一用一份来维护
 *
 * @author luming
 * date 2022/3/9
 */
public enum JobStatus {
    /**
     * 新建
     */
    NEW,
    /**
     * 等待
     */
    PENDING,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 停止
     */
    STOPPED,
    /**
     * 运行结束/成功
     */
    FINISHED,
    /**
     * 运行失败
     */
    ERROR,
    /**
     * 丢弃
     */
    DISCARDED,
    /**
     * 被挂起
     */
    SUSPENDED,
    /**
     * 被取消
     */
    CANCELLED;

    JobStatus() {
    }

    public static String getStatus(String statusStr) {
        //重复状态适配
        switch (statusStr) {
            case "WAITING":
                return JobStatus.PENDING.name();
            case "SUCCESS":
                return JobStatus.FINISHED.name();
            case "FAILED":
                return JobStatus.ERROR.name();
            default:
        }
        return JobStatus.valueOf(statusStr).name();
    }
}
