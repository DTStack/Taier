package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2022/1/27 11:42 AM
 * @Email: dazhi@dtstack.com
 * @Description: 返回可依赖的任务
 */
@ApiModel("返回可依赖的任务")
public class DevelopAllProductGlobalReturnVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example = "123")
    private String taskName;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", example = "1")
    private Long tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称", example = "1")
    private String tenantName;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
