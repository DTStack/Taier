package com.dtstack.batch.enums;

/**
 * @author yunliu
 * @date 2020-04-28 09:59
 * @description sql的类型枚举类（0：不走selectdata接口；1：走selectdata接口）
 */
public enum SqlTypeEnums {

    /**
     * 不走selectdata接口
     */
    NO_SELECT_DATA(0),

    /**
     * 走selectdata接口
     */
    SELECT_DATA(1);

    private Integer type;

    SqlTypeEnums(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
