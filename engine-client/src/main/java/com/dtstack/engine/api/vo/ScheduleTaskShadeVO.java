package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
@ApiModel
public class ScheduleTaskShadeVO extends ScheduleTaskShade {

    private Long tenantId;

    private Long projectId;

    private Long taskModifyUserId;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer pageSize = 10;

    private Integer pageIndex = 1;

    private String taskName;

    private String sort = "desc";

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskModifyUserId() {
        return taskModifyUserId;
    }

    public void setTaskModifyUserId(Long taskModifyUserId) {
        this.taskModifyUserId = taskModifyUserId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
