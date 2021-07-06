package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author jiangbo
 * @time 2018/1/8
 */
@Data
public class BatchTableInfoDTO {

    private Long tenantId;

    private Long projectId;

    private List<Long> tableIds;

    private String tableName;

    private Integer tableType;

    private List<String> tableNames;

    private Integer isDeleted = 0;

    private Integer isDirtyDataTable;

    private String grade;
    private String subject;
    private String refreshRate;
    private String increType;
    private Integer ignore;
    private List<Integer> triggerType;
    private Integer lifeStatus;

    /**
     * 时间段过滤
     */
    private Timestamp mdfBeginTime;

    private Timestamp mdfEndTime;

    private Long modifyUserId;

    /**
     * 表责任人
     */
    private Long chargeUserId;

}
