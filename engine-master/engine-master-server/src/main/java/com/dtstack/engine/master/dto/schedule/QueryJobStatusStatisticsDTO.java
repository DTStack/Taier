package com.dtstack.engine.master.dto.schedule;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/24 2:04 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobStatusStatisticsDTO {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 用户ID 责任人
     */
    private Long userId;

    /**
     * 计划开始时间
     **/
    private Long cycStartDay;

    /**
     * 计划结束时间
     **/
    private Long cycEndDay;

    /**
     * 任务类型
     */
    private List<Integer> taskTypeList;

    /**
     * 状态
     */
    private List<Integer> jobStatusList;

    /**
     * 调度周期类型
     */
    private List<Integer> taskPeriodTypeList;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCycStartDay() {
        return cycStartDay;
    }

    public void setCycStartDay(Long cycStartDay) {
        this.cycStartDay = cycStartDay;
    }

    public Long getCycEndDay() {
        return cycEndDay;
    }

    public void setCycEndDay(Long cycEndDay) {
        this.cycEndDay = cycEndDay;
    }

    public List<Integer> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<Integer> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Integer> getJobStatusList() {
        return jobStatusList;
    }

    public void setJobStatusList(List<Integer> jobStatusList) {
        this.jobStatusList = jobStatusList;
    }

    public List<Integer> getTaskPeriodTypeList() {
        return taskPeriodTypeList;
    }

    public void setTaskPeriodTypeList(List<Integer> taskPeriodTypeList) {
        this.taskPeriodTypeList = taskPeriodTypeList;
    }
}
