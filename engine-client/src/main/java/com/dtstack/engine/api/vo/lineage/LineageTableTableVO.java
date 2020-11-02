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

    @ApiModelProperty("输入表id")
    private LineageTableVO inputTableInfo;

    @ApiModelProperty("输出表id")
    private LineageTableVO resultTableInfo;

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
