package com.dtstack.engine.api.vo.lineage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableTableVO
 * @Description 表级血缘信息
 * @Date 2020/10/30 10:09
 * @Created chener@dtstack.com
 */
@ApiModel("表级血缘关系")
public class LineageTableTableVO {

    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    @ApiModelProperty("app类型")
    private Integer appType;

    @ApiModelProperty("输入表id")
    private LineageTableVO inputTableInfo;

    @ApiModelProperty("输出表id")
    private LineageTableVO resultTableInfo;

    @ApiModelProperty(value = "使用双亲表示法，标识树数据结构",notes = "当前节点的父节点在列表中的下标")
    private Integer parentIndex;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public LineageTableVO getInputTableInfo() {
        return inputTableInfo;
    }

    public void setInputTableInfo(LineageTableVO inputTableInfo) {
        this.inputTableInfo = inputTableInfo;
    }

    public LineageTableVO getResultTableInfo() {
        return resultTableInfo;
    }

    public void setResultTableInfo(LineageTableVO resultTableInfo) {
        this.resultTableInfo = resultTableInfo;
    }

    public Integer getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(Integer parentIndex) {
        this.parentIndex = parentIndex;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }
}
