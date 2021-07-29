package com.dtstack.batch.common.enums;

public enum TransformConvertType {

    /**
     * 表名
     */
    TABLE(1),
    /**
     * 字段名
     */
    COLUMNNAME(2),
    /**
     * 字段类型
     */
    COLUMNTYPE(3);

    private Integer type;
    public Integer getType() {
        return type;
    }
    TransformConvertType(Integer type) {
        this.type = type;
    }
}
