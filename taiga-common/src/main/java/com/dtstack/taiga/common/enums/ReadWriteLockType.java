package com.dtstack.taiga.common.enums;

public enum ReadWriteLockType {
    /**
     * 离线计算任务修改读些锁（控制多端修改同一个任务）
     */
    BATCH_TASK(1, "batch_task"),

    /**
     * 离线计算脚本修改读些锁
     */
    BATCH_SCRIPT(2, "batch_script"),

    /**
     * 流计算任务读写锁
     */
    STREAM_TASK(3, "stream_task"),

    /**
     * 离线计算表读写锁
     */
    BATCH_TABLE(4, "batch_table"),

    /**
     * 离线计算组件读写锁
     */
    BATCH_COMPONENT(5, "batch_component")
    ;

    private Integer type;
    private String text;

    ReadWriteLockType(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
