package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchTableColumn extends TenantProjectEntity {

    private Long tableId;

    private String columnName;

    private String columnType;

    private String columnDesc;

    private Integer columnIndex;

    private Integer isIgnore;

    private String checkResult;

    public Integer getIsIgnore() {
        return isIgnore;
    }

    public void setIsIgnore(Integer isIgnore) {
        this.isIgnore = isIgnore;
    }
}
