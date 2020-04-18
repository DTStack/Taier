package com.dtstack.engine.api.vo;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class KillJobVO {

    private Long tenantId;

    private Long projectId;
    /**
     *  业务日期
     */
    private Long bizStartDay;

    /**
     * 业务日期
     */
    private Long bizEndDay;

    /**
     * 0周期任务；1补数据实例。
     */
    private Integer type;

    /**
     * 调度周期,用逗号分割
     */
    private String taskPeriodId;

    /**
     * 任务状态，不能是成功状态。用逗号分割
     */
    private String jobStatuses;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
