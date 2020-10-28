package com.dtstack.engine.api.domain;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRef
 * @Description 表血缘应用关联表
 * @Date 2020/10/28 19:46
 * @Created chener@dtstack.com
 */
public class LineageTableTableAppRef extends BaseEntity{

    private Integer appType;

    private Integer lineageTableTableId;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getLineageTableTableId() {
        return lineageTableTableId;
    }

    public void setLineageTableTableId(Integer lineageTableTableId) {
        this.lineageTableTableId = lineageTableTableId;
    }
}
