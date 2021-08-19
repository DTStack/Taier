package com.dtstack.engine.api.vo.task;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/5/18 10:30 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskGetSupportJobTypesResultVO {

    //"任务类型", example = "0"
    private Integer key;

    // "任务描述", example = "SparkSQL"
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
}
