package com.dtstack.engine.api.vo.lineage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageDataSourceInfo
 * @Description
 * @Date 2020/10/30 10:10
 * @Created chener@dtstack.com
 */
@ApiModel("数据源信息")
public class LineageDataSourceVO {

    /**
     * 应用类型
     */
    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    /**
     * 数据源中心id
     */
    @ApiModelProperty("数据源中心id")
    private Long dataInfoId;

    /**
     * 数据源名称
     */
    @ApiModelProperty("数据源名称")
    private String sourceName;

    /**
     * 数据源类型
     */
    @ApiModelProperty("数据源类型")
    private Integer sourceType;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
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
