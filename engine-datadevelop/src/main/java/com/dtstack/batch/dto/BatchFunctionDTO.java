package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchFunctionDTO {

    private Long tenantId;

    private Long projectId;

    private Long functionModifyUserId;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer pageSize = 10;

    private Integer pageIndex = 1;

    private String functionName;

    private String sort = "desc";

    private Integer type;

}
