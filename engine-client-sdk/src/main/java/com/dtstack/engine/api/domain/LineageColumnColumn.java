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
public class LineageColumnColumn extends DtUicTenantEntity {

    @ApiModelProperty("应用类型")
    private Integer appType;

    @ApiModelProperty("输入物理表定位key")
    private String inputTableKey;

    @ApiModelProperty(notes = "输入表id")
    private Long inputTableId;

    @ApiModelProperty(notes = "输入字段名")
    private String inputColumnName;

    @ApiModelProperty("输出物理表定位key")
    private String resultTableKey;

    @ApiModelProperty(notes = "输出表id")
    private Long resultTableId;

    @ApiModelProperty(notes = "输出字段名称")
    private String resultColumnName;

    @ApiModelProperty(notes = "血缘定位码")
    private String columnLineageKey;

    @ApiModelProperty(notes = "血缘来源：0-sql解析；1-手动维护；2-json解析")
    private Integer lineageSource;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getInputTableId() {
        return inputTableId;
    }

    public void setInputTableId(Long inputTableId) {
        this.inputTableId = inputTableId;
    }

    public String getInputColumnName() {
        return inputColumnName;
    }

    public void setInputColumnName(String inputColumnName) {
        this.inputColumnName = inputColumnName;
    }

    public Long getResultTableId() {
        return resultTableId;
    }

    public void setResultTableId(Long resultTableId) {
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

    public Integer getLineageSource() {
        return lineageSource;
    }

    public void setLineageSource(Integer lineageSource) {
        this.lineageSource = lineageSource;
    }

    public String getInputTableKey() {
        return inputTableKey;
    }

    public void setInputTableKey(String inputTableKey) {
        this.inputTableKey = inputTableKey;
    }

    public String getResultTableKey() {
        return resultTableKey;
    }

    public void setResultTableKey(String resultTableKey) {
        this.resultTableKey = resultTableKey;
    }
}
