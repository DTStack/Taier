package com.dtstack.engine.domain;

import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2021/8/12 11:05 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobFailed {
    private Long id;
    private Long uicTenantId;
    private Long projectId;
    private Long taskId;
    private Integer appType;
    private Date gmtCreate;
    private Integer errorCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUicTenantId() {
        return uicTenantId;
    }

    public void setUicTenantId(Long uicTenantId) {
        this.uicTenantId = uicTenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

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

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
}
