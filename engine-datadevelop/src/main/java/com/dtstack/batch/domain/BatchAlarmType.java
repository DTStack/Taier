package com.dtstack.batch.domain;


import com.dtstack.engine.api.domain.TenantProjectEntity;

public class BatchAlarmType extends TenantProjectEntity {

    // 告警规则id
    private Long alarmId;

    // 告警通道类别id
    private Long alarmTypeId;

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Long getAlarmTypeId() {
        return alarmTypeId;
    }

    public void setAlarmTypeId(Long alarmTypeId) {
        this.alarmTypeId = alarmTypeId;
    }
}
