package com.dtstack.engine.alert.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 2:58 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertRecordStatusEnum {

    NO_WARNING(0,"未告警"),
    ALARM_QUEUE(1,"告警队列中"),
    SENDING_ALARM(2,"告警发送中"),
    ALERT_SUCCESS(3,"告警成功"),
    TO_BE_SCANNED(4,"待扫描中"),
    ;

    private Integer type;

    private String msg;

    AlertRecordStatusEnum(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
