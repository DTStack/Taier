package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRef
 * @Description 字段级血缘应用关联表
 * @Date 2020/10/28 19:46
 * @Created chener@dtstack.com
 */
public class LineageColumnColumnAppRef extends BaseEntity{

    @ApiModelProperty(notes = "app类型")
    private Integer appType;

    @ApiModelProperty(notes = "字段血缘关联id")
    private Integer lineageColumnColumnId;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getLineageColumnColumnId() {
        return lineageColumnColumnId;
    }

    public void setLineageColumnColumnId(Integer lineageColumnColumnId) {
        this.lineageColumnColumnId = lineageColumnColumnId;
    }
}
