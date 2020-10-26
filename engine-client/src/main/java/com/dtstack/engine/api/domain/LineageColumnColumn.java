package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumn
 * @Description 字段级血缘关系表
 * @Date 2020/10/22 20:15
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageColumnColumn extends TenantEntity {
    @ApiModelProperty(notes = "app类型")
    private Integer appType;

    @ApiModelProperty(notes = "输入表id")
    private Integer inputTableId;

    @ApiModelProperty(notes = "输入字段名")
    private String inputColumnName;

    @ApiModelProperty(notes = "输出表id")
    private Integer resultTableId;

    @ApiModelProperty(notes = "输出字段名称")
    private String resultColumnName;

    @ApiModelProperty(notes = "血缘定位码")
    private String columnLineageKey;

    @ApiModelProperty(notes = "是否手动维护")
    private Integer isManual;

    @ApiModelProperty(notes = "血缘批次唯一码")
    private String uniqueKey;

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

    public String getInputColumnName() {
        return inputColumnName;
    }

    public void setInputColumnName(String inputColumnName) {
        this.inputColumnName = inputColumnName;
    }

    public Integer getResultTableId() {
        return resultTableId;
    }

    public void setResultTableId(Integer resultTableId) {
        this.resultTableId = resultTableId;
    }

    public String getResultColumnName() {
        return resultColumnName;
    }

    public void setResultColumnName(String resultColumnName) {
        this.resultColumnName = resultColumnName;
    }

    public String getColumnLineageKey() {
        return columnLineageKey;
    }

    public void setColumnLineageKey(String columnLineageKey) {
        this.columnLineageKey = columnLineageKey;
    }

    public Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(Integer isManual) {
        this.isManual = isManual;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
