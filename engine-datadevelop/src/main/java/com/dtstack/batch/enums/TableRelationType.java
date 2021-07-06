package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @date 2018/5/26 16:39
 */
public enum TableRelationType {

    /**
     * 脚本
     */
    SCRIPT(0),

    /**
     * 任务
     */
    TASK(1);

    private Integer type;

    TableRelationType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
