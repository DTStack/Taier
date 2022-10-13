package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 1:36 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FillDataTypeEnum {
    BATCH(0, "批量生成"),
    PROJECT(1, "按照工程补数据"),
    ;
    private final Integer type;

    private final String name;

    FillDataTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
