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
}
