package com.dtstack.engine.api.vo;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/22 12:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobDetailsVO {

    private String name;

    private Integer taskType;

    private String tenantName;

    private Integer appType;

    private String projectName;

    private Integer taskRule;

    private List<ScheduleJobDetailsVO> scheduleJobDetailsVOList;

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

    public List<ScheduleJobDetailsVO> getScheduleJobDetailsVOList() {
        return scheduleJobDetailsVOList;
    }

    public void setScheduleJobDetailsVOList(List<ScheduleJobDetailsVO> scheduleJobDetailsVOList) {
        this.scheduleJobDetailsVOList = scheduleJobDetailsVOList;
    }
}
