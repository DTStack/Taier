package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2018/1/2
 */
@Data
public class BatchTableActionRecordDTO {

    private String sqlType;
    private Long actionUserId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Long tableId;
    private Long tenantId;
    private String operate;
    private String sql;

}
