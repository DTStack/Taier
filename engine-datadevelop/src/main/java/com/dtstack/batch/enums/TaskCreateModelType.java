package com.dtstack.batch.enums;

public enum TaskCreateModelType {

    /**
     * 向导模式
     */
    GUIDE(0),

    /**
     * 脚本模式
     */
    TEMPLATE(1);

    private Integer type;

    TaskCreateModelType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
