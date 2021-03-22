package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2021/3/18 9:12 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NotDeleteTaskVO {

    private String tenantName;

    private String projectAlias;

    private String projectName;

    private String taskName;

    private Integer appType;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getProjectAlias() {
        return projectAlias;
    }

    public void setProjectAlias(String projectAlias) {
        this.projectAlias = projectAlias;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
}
