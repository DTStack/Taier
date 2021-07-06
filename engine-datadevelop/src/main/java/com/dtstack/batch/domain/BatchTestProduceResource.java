package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchTestProduceResource extends BaseEntity {

    private Long tenantId;

    private Long produceTenantId;

    private Long testResourceId;

    private Long produceResourceId;

    private Integer resourceType;

}
