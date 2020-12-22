package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 7:44 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum SenderType {

    MAIL(1), SMS(2), WEICHAT(3), DINGDING(4),PHONE(5),CUSTOMIZE(6);

    private Integer type;

    SenderType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static SenderType parse(int type) {
        SenderType[] values = values();
        for (SenderType value : values) {
            if (type == value.type) {
                return value;
            }
        }
        return null;
    }

}
