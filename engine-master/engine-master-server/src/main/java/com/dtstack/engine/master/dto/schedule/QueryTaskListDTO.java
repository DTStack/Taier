package com.dtstack.engine.master.dto.schedule;

import com.dtstack.engine.master.controller.param.PageParam;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 3:42 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryTaskListDTO {

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 所属用户
     */
    private Long ownerId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 调度状态
     */
    private Integer scheduleStatus;

    /**
     * 任务类型
     */
    private String taskTypeList;

    /**
     * 周期类型
     */
    private String periodTypeList;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页数
     */
    private Integer pageSize;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(String taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public String getPeriodTypeList() {
        return periodTypeList;
    }

    public void setPeriodTypeList(String periodTypeList) {
        this.periodTypeList = periodTypeList;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
