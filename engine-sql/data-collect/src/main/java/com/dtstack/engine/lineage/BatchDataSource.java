package com.dtstack.engine.lineage;

/**
 * @author chener
 * @Classname BatchDataSource
 * @Description TODO
 * @Date 2020/11/23 15:54
 * @Created chener@dtstack.com
 */
public class BatchDataSource {

    private Long sourceId;

    private String sourceName;

    private Long tenantId;

    private Integer sourceType;

    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}
