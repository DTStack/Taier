package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/12/8 4:30 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertGateTypeEnum {

    MAIL(1,"邮箱"), SMS(2,"短信"), DINGDING(3,"钉钉"),CUSTOMIZE(4,"自定义");

    private Integer type;

    private String msg;

    private static final String defaultFiled="default_";

    AlertGateTypeEnum(Integer type,String name) {
        this.type = type;
        this.msg = name;
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

    public static String getDefaultFiled(AlertGateTypeEnum alertGateTypeEnum){
        return defaultFiled+alertGateTypeEnum.name()+"_"+alertGateTypeEnum.type;
    }

    public static Integer isDefaultFile(String source) {
        AlertGateTypeEnum[] values = AlertGateTypeEnum.values();

        for (AlertGateTypeEnum value : values) {
            String defaultFiled = getDefaultFiled(value);

            if (defaultFiled.equals(source)) {
                return value.type;
            }
        }

        return null;
    }

    public Integer getType() {
        return type;
    }

}
