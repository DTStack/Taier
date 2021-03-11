package com.dtstack.engine.api.vo;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("杀死任务实例信息")
public class ScheduleJobKillJobVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "业务开始日期", example = "1609084800", required = true)
    private Long bizStartDay;

    @ApiModelProperty(value = "业务结束日期", example = "1609084800", required = true)
    private Long bizEndDay;

    @ApiModelProperty(value = "类型", example = "0周期任务；1补数据实例", required = true)
    private Integer type;

    @ApiModelProperty(value = "调度周期", required = true)
    private String taskPeriodId;

    @ApiModelProperty(value = "任务状态", required = true)
    private String jobStatuses;

    @ApiModelProperty(value = "应用类型",  required = true)
    private Integer appType;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBizStartDay() {
        return bizStartDay;
    }

    public void setBizStartDay(Long bizStartDay) {
        this.bizStartDay = bizStartDay;
    }

    public Long getBizEndDay() {
        return bizEndDay;
    }

    public void setBizEndDay(Long bizEndDay) {
        this.bizEndDay = bizEndDay;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(String taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public String getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
    }
}
