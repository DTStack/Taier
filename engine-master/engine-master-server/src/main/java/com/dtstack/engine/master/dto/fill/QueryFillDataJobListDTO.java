package com.dtstack.engine.master.dto.fill;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 4:31 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryFillDataJobListDTO {

    /**
     * 补数据id
     */
    @NotNull(message = "fillId is not null")
    private Long fillId;

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
     * 计算开始时间
     */
    private Long cycStartDay;

    /**
     * 计算结束时间
     */
    private Long cycEndDay;

    /**
     * 用户ID 责任人
     */
    private Long userId;

    /**
     * 任务类型
     */
    private List<Integer> taskTypeList;

    /**
     * 状态
     */
    private List<Integer> jobStatusList;

    /**
     * 按业务日期排序
     */
    private String businessDateSort;

    /**
     * 按计划时间排序
     */
    private String cycSort;

    /**
     * 按运行时长排序
     */
    private String execTimeSort;

    /**
     * 按开始时间排序
     */
    private String execStartSort;

    /**
     * 按重试次数排序
     */
    private String retryNumSort;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 页码
     */
    private Integer pageSize;

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getBusinessDateSort() {
        return businessDateSort;
    }

    public void setBusinessDateSort(String businessDateSort) {
        this.businessDateSort = businessDateSort;
    }

    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
    }

    public String getExecTimeSort() {
        return execTimeSort;
    }

    public void setExecTimeSort(String execTimeSort) {
        this.execTimeSort = execTimeSort;
    }

    public String getExecStartSort() {
        return execStartSort;
    }

    public void setExecStartSort(String execStartSort) {
        this.execStartSort = execStartSort;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryFillDataJobListDTO that = (QueryFillDataJobListDTO) o;
        return Objects.equals(fillId, that.fillId) && Objects.equals(tenantId, that.tenantId) && Objects.equals(taskName, that.taskName) && Objects.equals(cycStartDay, that.cycStartDay) && Objects.equals(cycEndDay, that.cycEndDay) && Objects.equals(userId, that.userId) && Objects.equals(taskTypeList, that.taskTypeList) && Objects.equals(jobStatusList, that.jobStatusList) && Objects.equals(businessDateSort, that.businessDateSort) && Objects.equals(cycSort, that.cycSort) && Objects.equals(execTimeSort, that.execTimeSort) && Objects.equals(execStartSort, that.execStartSort) && Objects.equals(retryNumSort, that.retryNumSort) && Objects.equals(currentPage, that.currentPage) && Objects.equals(pageSize, that.pageSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fillId, tenantId, taskName, cycStartDay, cycEndDay, userId, taskTypeList, jobStatusList, businessDateSort, cycSort, execTimeSort, execStartSort, retryNumSort, currentPage, pageSize);
    }

    @Override
    public String toString() {
        return "FillDataJobListDTO{" +
                "fillId=" + fillId +
                ", tenantId=" + tenantId +
                ", taskName='" + taskName + '\'' +
                ", bizStartDay=" + cycStartDay +
                ", bizEndDay=" + cycEndDay +
                ", userId=" + userId +
                ", taskTypeList=" + taskTypeList +
                ", jobStatusList=" + jobStatusList +
                ", businessDateSort='" + businessDateSort + '\'' +
                ", cycSort='" + cycSort + '\'' +
                ", execTimeSort='" + execTimeSort + '\'' +
                ", execStartSort='" + execStartSort + '\'' +
                ", retryNumSort='" + retryNumSort + '\'' +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
