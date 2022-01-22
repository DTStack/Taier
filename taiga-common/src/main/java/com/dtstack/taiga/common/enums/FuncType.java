package com.dtstack.taiga.common.enums;

/**
 * jiangbo
 */
public enum FuncType {
    /**
     * 自定义函数
     */
    CUSTOM(0),
    /**
     * 系统函数
     */
    SYSTEM(1),
    /**
     * 存储过程
     */
    PROCEDURE(2);

    private Integer type;

    FuncType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

}
