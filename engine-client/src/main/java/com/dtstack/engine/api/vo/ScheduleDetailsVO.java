package com.dtstack.engine.api.vo;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/22 12:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleDetailsVO {

    private String name;

    private Integer taskType;

    private String tenantName;

    private Integer appType;

    private String projectName;

    private Integer taskRule;

    private Integer scheduleStatus;

    private List<ScheduleDetailsVO> scheduleDetailsVOList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }

    public List<ScheduleDetailsVO> getScheduleDetailsVOList() {
        return scheduleDetailsVOList;
    }

    public void setScheduleDetailsVOList(List<ScheduleDetailsVO> scheduleDetailsVOList) {
        this.scheduleDetailsVOList = scheduleDetailsVOList;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
