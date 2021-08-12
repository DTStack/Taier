package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.BaseEntity;
import lombok.Data;

@Data
public class BatchTestProduceTask extends BaseEntity {

    private Long tenantId;

    private Long testTaskId;

    private Long produceTaskId;
}
