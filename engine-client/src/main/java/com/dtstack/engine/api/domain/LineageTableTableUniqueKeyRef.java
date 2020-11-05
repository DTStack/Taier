package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRef
 * @Description 表血缘应用关联表
 * @Date 2020/10/28 19:46
 * @Created chener@dtstack.com
 */
public class LineageTableTableUniqueKeyRef extends DtUicTenantEntity{

    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    @ApiModelProperty(notes = "血缘批次码，离线中通常为taskId")
    private String uniqueKey;

    @ApiModelProperty(notes = "表血缘关联id")
    private Long lineageTableTableId;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getLineageTableTableId() {
        return lineageTableTableId;
    }

    public void setLineageTableTableId(Long lineageTableTableId) {
        this.lineageTableTableId = lineageTableTableId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
