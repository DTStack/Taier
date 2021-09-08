package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRef
 * @Description 字段级血缘应用关联表
 * @Date 2020/10/28 19:46
 * @Created chener@dtstack.com
 */
public class LineageColumnColumnUniqueKeyRef extends BaseEntity{

    @ApiModelProperty(notes = "app类型")
    private Integer appType;

    @ApiModelProperty(notes = "血缘批次码，离线中通常为taskId")
    private String uniqueKey;

    @ApiModelProperty(notes = "字段血缘关联id")
    private Long lineageColumnColumnId;

    /**任务提交版本号**/
    private Integer versionId;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getLineageColumnColumnId() {
        return lineageColumnColumnId;
    }

    public void setLineageColumnColumnId(Long lineageColumnColumnId) {
        this.lineageColumnColumnId = lineageColumnColumnId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }
}
