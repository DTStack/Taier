package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class NotifyUser extends TenantProjectEntity {
    /**
     * 通知id
     */
    private Long notifyId;
    /**
     * 接收人id
     */
    private Long userId;

}
