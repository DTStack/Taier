package com.dtstack.engine.alert.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Date: 2020/5/22
 * Company: www.dtstack.com
 * 告警通道大类
 *
 * @author xiaochen
 */
public enum AGgateType {
    AG_GATE_TYPE_SMS(1, "sms"),
    AG_GATE_TYPE_MAIL(2, "mail"),
    AG_GATE_TYPE_DING(3, "ding"),
    AG_GATE_TYPE_PHONE(5, "phone"),
    ;

    private int type;
    private String value;

    AGgateType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int type() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static AGgateType parse(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("value should not be blank");
        }
        for (AGgateType aGgateType : values()) {
            if (aGgateType.value.equals(value)) {
                return aGgateType;
            }
        }
        throw new IllegalArgumentException("unsupported value " + value);
    }
}
