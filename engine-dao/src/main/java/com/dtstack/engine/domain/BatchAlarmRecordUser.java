package com.dtstack.engine.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchAlarmRecordUser extends AppTenantEntity {

    private Long alarmRecordId;

    private Long userId;

    public Long getAlarmRecordId() {
        return alarmRecordId;
    }

    public void setAlarmRecordId(Long alarmRecordId) {
        this.alarmRecordId = alarmRecordId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
