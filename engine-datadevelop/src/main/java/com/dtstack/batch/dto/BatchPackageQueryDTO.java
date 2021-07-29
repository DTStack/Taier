package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchPackageQueryDTO {

    private Long tenantId;

    private Long projectId;

    private Integer pageSize = 10;

    private Integer pageIndex = 1;

    private String orderBy = "gmt_create";

    private String sort = "desc";

    private Long publishUserId;

    private Long applyUserId;

    private Timestamp publishTimeStart;

    private Timestamp publishTimeEnd;

    private Timestamp applyTimeStart;

    private Timestamp applyTimeEnd;

    private String packageName;

    private Integer status;

    private Integer packageType;

}
