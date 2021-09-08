package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class BatchAlarmReceiveUser extends TenantProjectEntity {

    /**
     * 告警id
     */
    private Long alarmId;

    /**
     * 告警接收人id
     */
    private Long userId;

}
