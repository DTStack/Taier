package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2018/1/3
 */
@Data
public class BatchHivePartitionDTO {

    private Long tenantId;

    private Long projectId;

    private Long tableId;

    private Timestamp start;

    private Timestamp end;

}
