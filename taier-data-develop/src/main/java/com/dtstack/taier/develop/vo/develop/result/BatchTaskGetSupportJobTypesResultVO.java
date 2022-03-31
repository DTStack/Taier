package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("支持的引擎类型")
public class BatchTaskGetSupportJobTypesResultVO {

    @ApiModelProperty(value = "任务类型", example = "0")
    private Integer key;

    @ApiModelProperty(value = "任务描述", example = "SparkSQL")
    private String value;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BatchTaskGetSupportJobTypesResultVO(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public BatchTaskGetSupportJobTypesResultVO() {
    }
}
