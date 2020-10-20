package com.dtstack.engine.alert.enums;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Date: 2020/5/22
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public enum AlertGateCode {

    AG_GATE_SMS_YP("sms_yp"),
    AG_GATE_SMS_DY("sms_dy"),
    AG_GATE_SMS_API("sms_API"),

    AG_GATE_MAIL_DT("mail_dt"),
    AG_GATE_MAIL_API("mail_api"),

    AG_GATE_DING_DT("ding_dt"),
    AG_GATE_DING_API("ding_api"),

    AG_GATE_SMS_JAR("sms_jar"),
    AG_GATE_MAIL_JAR("mail_jar"),
    AG_GATE_DING_JAR("ding_jar"),

    AG_GATE_PHONE_TC("phone_tc"),
    ;

    private String code;

    AlertGateCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static AlertGateCode parse(String code) {
        Assert.isTrue(StringUtils.isNotBlank(code),"code should not be empty");
        AlertGateCode[] values = values();
        for (AlertGateCode value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unsupported code " + code);
    }

}
