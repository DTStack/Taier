package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableTable
 * @Description TODO
 * @Date 2020/10/22 20:15
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageTableTable extends TenantEntity {

    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    @ApiModelProperty(notes = "输入表id")
    private Integer inputTableId;

    @ApiModelProperty(notes = "输出表id")
    private Integer resultTableId;

    @ApiModelProperty(notes = "表级血缘关系定位码")
    private String tableLineageKey;

    @ApiModelProperty(notes = "是否手动维护")
    private Integer isManual;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getInputTableId() {
        return inputTableId;
    }

    public void setInputTableId(Integer inputTableId) {
        this.inputTableId = inputTableId;
    }

    public Integer getResultTableId() {
        return resultTableId;
    }

    public void setResultTableId(Integer resultTableId) {
        this.resultTableId = resultTableId;
    }

    public String getTableLineageKey() {
        return tableLineageKey;
    }

    public void setTableLineageKey(String tableLineageKey) {
        this.tableLineageKey = tableLineageKey;
    }

    public Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(Integer isManual) {
        this.isManual = isManual;
    }
}
