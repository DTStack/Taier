package com.dtstack.batch.dto;

import lombok.Data;

/**
 * @author jiangbo
 * @date 2018/6/7 9:41
 */
@Data
public class BatchTableBloodDTO {
    private String tableName;
    private String col;
    private Long projectId;
    private Long dataSourceId;
    private Long tenantId;
    private Integer isGroupBy;

    public Integer getIsGroupBy() {
        return isGroupBy;
    }

    public void setIsGroupBy(Integer isGroupBy) {
        this.isGroupBy = isGroupBy;
    }

}
