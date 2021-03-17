package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/1/13 3:31 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertMessageStatusEnum {
    ALTER(0,"以告警"),NO_ALTER(1,"未告警");

    private Integer type;

    private String name;

    AlertMessageStatusEnum(int type, String name) {
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
