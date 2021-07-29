package com.dtstack.batch.domain;

import lombok.Data;

/**
 * 收藏表
 * @author jiangbo
 * @time 2018/5/19
 */
@Data
public class BatchTableCollection extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private Long userId;

    private Long tableId;

}
