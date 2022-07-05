package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ：zhaiyue
 * @date ：2022/06/29 23:15
 * @description：
 */
public class DevelopTenantComponentResultVO {

    @ApiModelProperty(value = "任务类型", example = "0")
    private Integer taskType;

    @ApiModelProperty(value = "任务类型名称", example = "SparkSQl")
    private String taskTypeName;

    @ApiModelProperty(value = "任务使用的schema", example = "dev")
    private String schema;

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
