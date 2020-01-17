package com.dtstack.task.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public class NotifyAlarm extends AppTenantEntity {
    /**
     * 业务类型，1：实时，2：离线
     */
    private Integer bizType;

    private Long notifyId;

    private Long AlarmId;

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(Long notifyId) {
        this.notifyId = notifyId;
    }

    public Long getAlarmId() {
        return AlarmId;
    }

    public void setAlarmId(Long alarmId) {
        AlarmId = alarmId;
    }
}
