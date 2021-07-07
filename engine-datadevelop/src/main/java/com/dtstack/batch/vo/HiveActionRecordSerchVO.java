package com.dtstack.batch.vo;

import lombok.Data;

/**
 * author: jiangbo
 * create: 2017/7/7.
 */
@Data
public class HiveActionRecordSerchVO {

    private Long tenantId;
    private Long projectId;
    private Long userId;
    private Integer pageSize = 10;
    private Integer pageIndex = 1;
    private String operate;
    private Long actionUserId;
    private Long startTime;
    private Long endTime;
    private Long tableId;

    private String sql;
}
