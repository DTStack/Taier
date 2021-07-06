package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchTestProduceTask extends BaseEntity {

    private Long tenantId;

    private Long testTaskId;

    private Long produceTaskId;
}
