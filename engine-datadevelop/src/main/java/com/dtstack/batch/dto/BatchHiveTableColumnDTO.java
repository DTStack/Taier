package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class BatchHiveTableColumnDTO {

    private String tableName;

    private List<Integer> triggerType;

    private Integer ignore;

    private Long projectId;

    private Long tenantId;

    private Integer isDeleted;

    private String columnName;

    /**
     * 时间段过滤
     */
    private Timestamp mdfBeginTime;

    private Timestamp mdfEndTime;

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }


}
