package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchResourceDTO {

    private Long tenantId;

    private Long projectId;

    private Long resourceModifyUserId;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer pageSize = 10;

    private Integer pageIndex = 1;

    private String resourceName;

    private String sort = "desc";

}
