package com.dtstack.engine.api.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class QueryJobDTO {

    /**项目id**/
    private Long projectId;

    /**租户id**/
    private Long tenantId;

    /**业务开始日期，0点秒级时间戳**/
    private Long bizStartDay;

    /**业务结束日期，0点秒级时间戳**/
    private Long bizEndDay;
    private String jobStatuses;
    private String taskName;

    /**任务类型,多个以逗号区分 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL**/
    private String taskType;

    private Long ownerId;

    /**0正常调度 1补数据 2临时运行**/
    private Integer type;
    private List<Integer> types;
    private Integer currentPage;
    private Integer pageSize;
    private Long execTime;
    private String execTimeSort;
    private String execStartSort;
    private String execEndSort;
    private String cycSort;
    private String businessDateSort;
    private String retryNumSort;
    private String taskPeriodId;
    private String fillTaskName;

    /**计划开始时间**/
    private Long cycStartDay;

    /**计划结束时间**/
    private Long cycEndDay;
    private Long execStartDay;
    private Long execEndDay;

    /** 增加失败状态细分标志**/
    private Boolean splitFiledFlag;

    /**1、全模糊匹配，2、精确查询，3、右模糊匹配，4、左模糊匹配**/
    private String searchType;

    /**子平台类型，见AppType枚举**/
    private Integer appType;
    private List<Long> projectIds;
    private Long taskId;
    private List<Long> taskIds;

    private String businessType;

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }

    public Boolean getSplitFiledFlag() {
        return splitFiledFlag;
    }

    public void setSplitFiledFlag(Boolean splitFiledFlag) {
        this.splitFiledFlag = splitFiledFlag;
    }

    public Long getCycEndDay() {
        return cycEndDay;
    }

    public void setCycEndDay(Long cycEndDay) {
        this.cycEndDay = cycEndDay;
    }

    public Long getCycStartDay() {

        return cycStartDay;
    }

    public void setCycStartDay(Long cycStartDay) {
        this.cycStartDay = cycStartDay;
    }

    public String getFillTaskName() {
        return fillTaskName;
    }

    public void setFillTaskName(String fillTaskName) {
        this.fillTaskName = fillTaskName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBizStartDay() {
        return bizStartDay;
    }

    public void setBizStartDay(Long bizStartDay) {
        this.bizStartDay = bizStartDay;
    }

    public Long getBizEndDay() {
        return bizEndDay;
    }

    public void setBizEndDay(Long bizEndDay) {
        this.bizEndDay = bizEndDay;
    }

    public String getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
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

    public String getExecEndSort() {
        return execEndSort;
    }

    public void setExecEndSort(String execEndSort) {
        this.execEndSort = execEndSort;
    }

    public String getCycSort() {
        return cycSort;
    }

    public void setCycSort(String cycSort) {
        this.cycSort = cycSort;
    }

    public String getBusinessDateSort() {
        return businessDateSort;
    }

    public void setBusinessDateSort(String businessDateSort) {
        this.businessDateSort = businessDateSort;
    }

    public String getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(String taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public Long getExecStartDay() {
        return execStartDay;
    }

    public void setExecStartDay(Long execStartDay) {
        this.execStartDay = execStartDay;
    }

    public Long getExecEndDay() {
        return execEndDay;
    }

    public void setExecEndDay(Long execEndDay) {
        this.execEndDay = execEndDay;
    }
}
