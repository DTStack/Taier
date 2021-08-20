package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 10:07 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum IsDeletedEnum {
    // 0正常 1逻辑删除
    NOT_DELETE(0, "正常"),DELETE(1,"逻辑删除");

    private Integer type;

    private String desc;

    IsDeletedEnum(Integer type, String desc) {
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
