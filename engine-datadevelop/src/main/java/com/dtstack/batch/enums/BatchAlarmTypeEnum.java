package com.dtstack.batch.enums;

/**
 * 告警类型
 */
public enum BatchAlarmTypeEnum {

    /**
     * 项目运行报告
     */
    PROJECT(1),
    /**
     * 其他
     */
    OTHER(2);

    Integer type;

    BatchAlarmTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
