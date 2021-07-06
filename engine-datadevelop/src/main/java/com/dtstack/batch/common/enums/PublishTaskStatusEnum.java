package com.dtstack.batch.common.enums;

/**
 * 任务提交状态  判断使用
 */
public enum  PublishTaskStatusEnum {
    /**
     * 无错误
     */
    NOMAL(0),
    /**
     * 权限校验错误
     */
    PERMISSIONERROR(1),
    /**
     * 语法校验错误
     */
    CHECKSYNTAXERROR(2);

    private Integer type;

    PublishTaskStatusEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
