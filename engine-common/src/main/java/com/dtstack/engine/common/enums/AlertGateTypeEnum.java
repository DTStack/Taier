package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/12/8 4:30 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertGateTypeEnum {

    MAIL(1), SMS(2), DINGDING(3),CUSTOMIZE(4);

    private Integer type;

    AlertGateTypeEnum(Integer type) {
        this.type = type;
    }

    public static String getEnumByCode(Integer alertGateType) {
        AlertGateTypeEnum[] values = AlertGateTypeEnum.values();

        for (AlertGateTypeEnum value : values) {
            if (value.getType().equals(alertGateType)) {
                return value.name();
            }
        }

        return null;
    }

    public Integer getType() {
        return type;
    }

}
