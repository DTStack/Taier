package com.dtstack.batch.common.enums;

public enum TransformConvertObject {

    /**
     * 替换
     */
    REPLACE(1),
    /**
     * 前缀
     */
    PREFIX(2),
    /**
     * 后缀
     */
    SUFFIX(3);

    private Integer type;
    public Integer getType() {
        return type;
    }
    TransformConvertObject(Integer type) {
        this.type = type;
    }

}
