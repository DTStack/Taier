package com.dtstack.engine.domain.po;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/24 2:23 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobsStatusStatisticsPO {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 用户ID 责任人
     */
    private Long userId;

    /**
     * 计划开始时间
     **/
    private String cycStartTime;

    /**
     * 计划结束时间
     **/
    private String cycEndTime;

    /**
     * 任务id
     */
    private List<Long> taskIdList;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCycStartTime() {
        return cycStartTime;
    }

    public void setCycStartTime(String cycStartTime) {
        this.cycStartTime = cycStartTime;
    }

    public String getCycEndTime() {
        return cycEndTime;
    }

    public void setCycEndTime(String cycEndTime) {
        this.cycEndTime = cycEndTime;
    }

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
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
