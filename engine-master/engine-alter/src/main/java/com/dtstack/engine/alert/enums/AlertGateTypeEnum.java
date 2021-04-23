package com.dtstack.engine.alert.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/12/8 4:30 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertGateTypeEnum {

    SMS(1,"短信","sms"),
    MAIL(2,"邮箱","mail") ,
    DINGDING(3,"钉钉","ding"),
    CUSTOMIZE(4,"自定义","customize"),
    PHONE(5,"手机","phone"),
    ;


    private Integer type;

    private String msg;

    private String value;

    private static final String defaultFiled="default_";

    AlertGateTypeEnum(Integer type, String msg, String value) {
        this.type = type;
        this.msg = msg;
        this.value = value;
    }

    public static AlertGateTypeEnum getEnumByCode(Integer alertGateType) {
        AlertGateTypeEnum[] values = AlertGateTypeEnum.values();

        for (AlertGateTypeEnum value : values) {
            if (value.getType().equals(alertGateType)) {
                return value;
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

    public static List<String> transformSenderTypes(List<Integer> senderTypes) {
        List<String> alertGateSources = Lists.newArrayList();

        for (Integer senderType : senderTypes) {
            if (senderType.equals(2)) {
                alertGateSources.add(getDefaultFiled(SMS));
            }

            if (senderType.equals(1)) {
                alertGateSources.add(getDefaultFiled(MAIL));
            }

            if (senderType.equals(4)) {
                alertGateSources.add(getDefaultFiled(DINGDING));
            }

            if (senderType.equals(5)) {
                alertGateSources.add(getDefaultFiled(PHONE));
            }

            if (senderType.equals(6)) {
                alertGateSources.add(getDefaultFiled(CUSTOMIZE));
            }
        }
        return alertGateSources;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getValue() {
        return value;
    }
}
