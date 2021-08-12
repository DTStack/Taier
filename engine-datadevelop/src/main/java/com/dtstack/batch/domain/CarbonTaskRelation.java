package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.BaseEntity;
import lombok.Data;

/**
 * @author mading
 * @create 2019-02-25 4:12 PM
 */
@Data
public class CarbonTaskRelation extends BaseEntity {

    private Long taskId;

    private Long sourceId;
}
