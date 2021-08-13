package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class NotifyAlarm extends TenantProjectEntity {
    /**
     * 业务类型，1：实时，2：离线
     */
    private Integer bizType;

    private Long notifyId;

    private Long AlarmId;

}
