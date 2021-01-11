package com.dtstack.engine.lineage.asserts;

/**
 * @author chener
 * @Classname AssetTableDTO
 * @Description TODO
 * @Date 2020/11/30 10:28
 * @Created chener@dtstack.com
 */
public class AssetTableDTO {
    private String tableName;

    private Integer dataSourceType;

    private Long dataSourceId;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
}
