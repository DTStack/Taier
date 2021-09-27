package com.dtstack.batch.domain;

import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

@Data
public class BatchTestProduceDataSource extends BaseEntity {

    private Long tenantId;

    private Long testDataSourceId;

    private Long produceDataSourceId;

}
