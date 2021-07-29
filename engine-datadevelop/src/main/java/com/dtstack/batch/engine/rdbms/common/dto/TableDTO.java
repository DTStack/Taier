package com.dtstack.batch.engine.rdbms.common.dto;

import java.util.List;

/**
 * @author jiangbo
 */
public class TableDTO {
    private Long projectId;
    private Long tenantId;
    private Long userId;
    private String tableName;
    private String tableDesc;
    private List<ColumnDTO> columns;
    private List<ColumnDTO> addColumns;
    private List<ColumnDTO> partitionKeys;
    private String delim;
    private String location;
    private Integer lifeDay;
    private Long catalogueId;
    private String storedType;
    private Long tableId;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableDesc() {
        return tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public List<ColumnDTO> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnDTO> columns) {
        this.columns = columns;
    }

    public List<ColumnDTO> getAddColumns() {
        return addColumns;
    }

    public void setAddColumns(List<ColumnDTO> addColumns) {
        this.addColumns = addColumns;
    }

    public List<ColumnDTO> getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(List<ColumnDTO> partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public String getDelim() {
        return delim;
    }

    public void setDelim(String delim) {
        this.delim = delim;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getLifeDay() {
        return lifeDay;
    }

    public void setLifeDay(Integer lifeDay) {
        this.lifeDay = lifeDay;
    }

    public Long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(Long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getStoredType() {
        return storedType;
    }

    public void setStoredType(String storedType) {
        this.storedType = storedType;
    }

    @Override
    public String toString() {
        return "TableDTO{" +
                "projectId=" + projectId +
                ", tenantId=" + tenantId +
                ", userId=" + userId +
                ", tableName='" + tableName + '\'' +
                ", tableDesc='" + tableDesc + '\'' +
                '}';
    }
}
