package com.dtstack.batch.enums;

/**
 * 告警类型
 */
public enum BatchAlarmType {

    /**
     * 项目运行报告
     */
    PROJECT(1),
    /**
     * 其他
     */
    OTHER(2);

    int type;

    BatchAlarmType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
