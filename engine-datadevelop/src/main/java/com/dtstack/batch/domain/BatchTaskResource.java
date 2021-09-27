package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class BatchTaskResource extends TenantProjectEntity {

    private Long taskId;

    private Long resourceId;

    private Integer resourceType;

}
