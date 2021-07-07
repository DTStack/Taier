package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * 用于配置治理规则时， 一次性治理 前端传入的一个vo
 */
@Data
public class FileMergeTableVO {

    /**
     * 表id hive_table_info表id
     */
    private Long tableId;

    /**
     * 选择治理的 分区的id
     */
    private List<Long> partitionIds;

}
