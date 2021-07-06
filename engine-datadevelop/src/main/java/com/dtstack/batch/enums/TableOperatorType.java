package com.dtstack.batch.enums;

/**
 * @author sanyue
 * @date 2018/11/20
 */
public enum TableOperatorType {

    /**
     * 全部操作
     */
    ALL(0),

    /**
     * DDL操作
     */
    DDL(1),

    /**
     * DML操作
     */
    DML(2);

    private Integer type;

    TableOperatorType (Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
