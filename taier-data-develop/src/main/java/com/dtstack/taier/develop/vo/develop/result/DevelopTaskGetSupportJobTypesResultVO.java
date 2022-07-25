package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("支持的引擎类型")
public class DevelopTaskGetSupportJobTypesResultVO {

    @ApiModelProperty(value = "任务类型", example = "0")
    private Integer key;

    @ApiModelProperty(value = "任务描述", example = "SparkSQL")
    private String value;

    @ApiModelProperty(value = "任务类型", example = "0：stream 1:batch")
    private Integer computeType;

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

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

    public DevelopTaskGetSupportJobTypesResultVO(Integer key, String value, Integer computeType) {
        this.key = key;
        this.value = value;
        this.computeType = computeType;
    }
    public DevelopTaskGetSupportJobTypesResultVO() {
    }
}
