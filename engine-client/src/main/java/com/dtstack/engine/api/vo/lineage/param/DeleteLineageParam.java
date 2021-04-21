package com.dtstack.engine.api.vo.lineage.param;/**
 * @author chenfeixiang6@163.com
 * @date 2021/4/16
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *类名称:DeleteLineageParam
 *类描述: 删除血缘传参
 *创建人:newman
 *创建时间:2021/4/16 10:37 上午
 *Version 1.0
 */
@ApiModel("删除血缘传参")
public class DeleteLineageParam {

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("平台类型")
    private Integer appType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}


