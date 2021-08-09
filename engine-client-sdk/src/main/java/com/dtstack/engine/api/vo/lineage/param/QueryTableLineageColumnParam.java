package com.dtstack.engine.api.vo.lineage.param;

/**
 * @author chener
 * @Classname QueryTableLineageColumnParam
 * @Description 查询表的血缘字段列表参数
 * @Date 2021/1/25 10:53
 * @Created chener@dtstack.com
 */
public class QueryTableLineageColumnParam {
    private Long tableId;

    /**数据源中心id**/
    private Long dataInfoId;

    private String sourceName;

    private Integer sourceType;

    private String dbName;

    private String tableName;

    private Integer appType;

    private Long dtUicTenantId;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }
}
