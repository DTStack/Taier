package com.dtstack.batch.dto;

import lombok.Data;

/**
 * 用于 分区信息查询的类
 */
@Data
public class HivePatitionSearchVO {

    private Long tenantId;
    private Long projectId;
    private Long userId;
    private Long tableId;
    private String sortColumn;
    private String sort;
    private Integer pageSize = 10;
    private Integer pageIndex = 1;
    private String partitionName;
}
