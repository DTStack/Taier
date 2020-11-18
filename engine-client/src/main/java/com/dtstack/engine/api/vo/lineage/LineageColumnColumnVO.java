package com.dtstack.engine.api.vo.lineage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumnVO
 * @Description 字段级血缘关系
 * @Date 2020/10/30 11:20
 * @Created chener@dtstack.com
 */
@ApiModel("字段级血缘关系")
public class LineageColumnColumnVO {

    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    /**
     * 应用类型
     */
    @ApiModelProperty("应用类型")
    private Integer appType;

    /**
     * 输入表信息
     */
    @ApiModelProperty("输入表信息")
    private LineageTableVO inputTableInfo;

    /**
     * 输入字段名
     */
    @ApiModelProperty("输入字段名称")
    private String inputColumnName;

    /**
     * 输出表信息
     */
    @ApiModelProperty("输出表信息")
    private LineageTableVO resultTableInfo;

    /**
     * 输出字段名
     */
    @ApiModelProperty("输出字段名称")
    private String resultColumnName;

    /**
     * 血缘批次唯一码
     */
    @ApiModelProperty("批次唯一码")
    private String uniqueKey;

    @ApiModelProperty(value = "使用双亲表示法，标识树数据结构",notes = "当前节点的父节点在列表中的下标")
    private Integer parentIndex;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getInputColumnName() {
        return inputColumnName;
    }

    public void setInputColumnName(String inputColumnName) {
        this.inputColumnName = inputColumnName;
    }

    public String getResultColumnName() {
        return resultColumnName;
    }

    public void setResultColumnName(String resultColumnName) {
        this.resultColumnName = resultColumnName;
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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
