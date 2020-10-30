package com.dtstack.engine.api.vo.lineage;

/**
 * @author chener
 * @Classname LineageDataSourceInfo
 * @Description
 * @Date 2020/10/30 10:10
 * @Created chener@dtstack.com
 */
public class LineageDataSourceVO {

    private Integer appType;

    private Long sourceId;

    private String sourceName;

    private Integer sourceType;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
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

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}
