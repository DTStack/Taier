package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 10:07 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum IsDefaultEnum {
    // 0正常 1逻辑删除
    NOT_DEFAULT(0, "非默认"),DEFAULT(1,"默认");

    private Integer type;

    private String desc;

    IsDefaultEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
