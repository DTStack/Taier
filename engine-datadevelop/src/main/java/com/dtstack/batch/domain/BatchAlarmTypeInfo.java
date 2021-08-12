package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;

public class BatchAlarmTypeInfo extends TenantProjectEntity {

    // 告警通道标识
    private String alertGateSource;

    // 告警通道名称
    private String alertGateName;

    // 告警通道类别
    private Integer alertGateType;

    public String getAlertGateSource() {
        return alertGateSource;
    }

    public void setAlertGateSource(String alertGateSource) {
        this.alertGateSource = alertGateSource;
    }

    public String getAlertGateName() {
        return alertGateName;
    }

    public void setAlertGateName(String alertGateName) {
        this.alertGateName = alertGateName;
    }

    public Integer getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(Integer alertGateType) {
        this.alertGateType = alertGateType;
    }
}
