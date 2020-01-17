package com.dtstack.engine.common.enums;

/**
 * 1：最后的db数据，2：队列最后的数据
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/6
 */
public enum SentinelType {

    NONE, END_DB, END_QUEUE;

    public boolean isSentinel() {
        return this != SentinelType.NONE;
    }

    public boolean isNone() {
        return this == SentinelType.NONE;
    }

    public boolean isEndDb() {
        return this == SentinelType.END_DB;
    }

    public boolean isEndQueue() {
        return this == SentinelType.END_QUEUE;
    }

}
