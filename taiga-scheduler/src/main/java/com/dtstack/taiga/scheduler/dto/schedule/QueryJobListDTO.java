package com.dtstack.taiga.scheduler.dto.schedule;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/23 4:18 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobListDTO {

    /**
     * 租户id
     */
    @NotNull(message = "tenantId is not null")
    private Long tenantId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 用户ID 责任人
     */
    private Long ownerId;

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

    /**
     * 按计划时间排序
     */
    private String cycSort;

    /**
     * 按开始时间排序
     */
    private String execStartSort;

    /**
     * 结束时间
     */
    private String execEndSort;

    /**
     * 按运行时长排序
     */
    private String execTimeSort;

    /**
     * 按重试次数排序
     */
    private String retryNumSort;

    /**
     * 当前页码
     */
    private Integer currentPage;

    /**
     * 当前页数
     */
    private Integer pageSize;

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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
    }

    public String getExecStartSort() {
        return execStartSort;
    }

    public void setExecStartSort(String execStartSort) {
        this.execStartSort = execStartSort;
    }

    public String getExecEndSort() {
        return execEndSort;
    }

    public void setExecEndSort(String execEndSort) {
        this.execEndSort = execEndSort;
    }

    public String getExecTimeSort() {
        return execTimeSort;
    }

    public void setExecTimeSort(String execTimeSort) {
        this.execTimeSort = execTimeSort;
    }

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
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
