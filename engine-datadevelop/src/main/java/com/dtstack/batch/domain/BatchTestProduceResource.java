package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.BaseEntity;
import lombok.Data;

@Data
public class BatchTestProduceResource extends BaseEntity {

    private Long tenantId;

    private Long produceTenantId;

    private Long testResourceId;

    private Long produceResourceId;

    private Integer resourceType;

}
