package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 数据预览结果
 */
@ApiModel("数据预览结果")
public class DsPollPreviewResultVO {

    @ApiModelProperty(value = "字段列表")
    private List<String> columnList;

    @ApiModelProperty(value = "数据信息")
    private Object dataList;

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public Object getDataList() {
        return dataList;
    }

    public void setDataList(Object dataList) {
        this.dataList = dataList;
    }


}
