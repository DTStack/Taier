package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/20.
 */
@Data
public class HivePartSearchVO {
    private Long tenantId;
    private Long dtuicTenantId;
    private Long projectId;
    private Long userId;
    private Long tableId;
    private String sortColumn;
    private String sort;
    private Long start;
    private Long end;
    private Integer pageSize = 10;
    private Integer pageIndex = 1;

    /**
     * 文件数量排序标示字段：
     * 0为升序 1为倒序
     */
    private Integer partFileCountOrder;

    /**
     * 需要过滤掉的分区信息
     */
    private List<String> filterPartitions;

}
