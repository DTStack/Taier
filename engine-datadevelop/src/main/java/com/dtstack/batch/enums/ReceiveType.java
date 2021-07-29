package com.dtstack.batch.enums;

/**
 * 项目运行报告接收人类型
 */
public enum ReceiveType {

    /**
     * admin
     */
    ADMIN(1),

    /**
     * other
     */
    OTHER(2);

    ReceiveType(Integer type) {
        this.type = type;
    }

    Integer type;

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
