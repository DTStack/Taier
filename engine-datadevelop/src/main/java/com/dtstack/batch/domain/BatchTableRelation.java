package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.BaseEntity;
import lombok.Data;

/**
 * @author jiangbo
 * @time 2017/12/7
 */
@Data
public class BatchTableRelation extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private Long dataSourceId;

    private String tableName;

    private Long tableId;

    private Long relationId;

    private Integer relationType;

    private Integer detailType;

    /**
     * 表在对应task中是否为任务表
     */
    private Integer relationResultType;

}
