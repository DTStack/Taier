/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.dao.dto;


import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/1
 */
public class ScheduleJobDTO {

    private List<Integer> jobStatuses;
    private String taskNameLike;
    private String businessDateLike;

    /**执行耗费时间**/
    private Long execTime;
    private String jobNameRightLike;
    private List<Long> taskIds;
    private Timestamp startGmtCreate;
    private Timestamp endGmtCreate;
    private Long taskCreateId;
    private List<Integer> taskTypes;
    private String execTimeSort;

    /**执行开始时间**/
    private String execStartSort;

    /**执行结束时间**/
    private String execEndSort;
    private String cycSort;
    private String retryNumSort;
    private String businessDateSort;
    private List<Integer> taskPeriodId;//任务周期列表
    private String bizStartDay;
    private String bizEndDay;
    /**调度开始日期**/
    private String cycStartDay;

    /**调度结束日期**/
    private String cycEndDay;
    private Long ownerUserId;
    private boolean pageQuery;

    /**查询工作流模式，  1.排除工作流子节点 2.只查询工作流子节点 3.父子节点都有查 4.排除工作流父节点 **/
    private Integer queryWorkFlowModel;
    private String fillDataJobName;
    private Integer  searchType;
    /**
     * fixme 算法实验任务实例Id
     */
    private List<String> labFlowJobIdList;

    /**
     * 是否查询子节点的任务类型
     */
    private boolean needQuerySonNode;

    private String likeBusinessDate;

    private Integer currentPage;

    private Integer pageSize;

    private Date execStartDay;

    private Date execEndDay;

    private List<Integer> types;
    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
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

    public String getRetryNumSort() {
        return retryNumSort;
    }

    public void setRetryNumSort(String retryNumSort) {
        this.retryNumSort = retryNumSort;
    }

    public boolean isNeedQuerySonNode() {
        return needQuerySonNode;
    }

    public void setNeedQuerySonNode(boolean needQuerySonNode) {
        this.needQuerySonNode = needQuerySonNode;
    }

    public List<String> getLabFlowJobIdList() {
        return labFlowJobIdList;
    }

    public void setLabFlowJobIdList(List<String> labFlowJobIdList) {
        this.labFlowJobIdList = labFlowJobIdList;
    }

    public String getFillDataJobName() {
        return fillDataJobName;
    }

    public void setFillDataJobName(String fillDataJobName) {
        this.fillDataJobName = fillDataJobName;
    }

    public Integer getQueryWorkFlowModel() {
        return queryWorkFlowModel;
    }

    public void setQueryWorkFlowModel(Integer queryWorkFlowModel) {
        this.queryWorkFlowModel = queryWorkFlowModel;
    }

    public boolean isPageQuery() {
        return pageQuery;
    }

    public void setPageQuery(boolean pageQuery) {
        this.pageQuery = pageQuery;
    }

    private Collection<String> jobIds;

    public Collection<String> getJobIds() {
        return jobIds;
    }

    public void setJobIds(Collection<String> jobIds) {
        this.jobIds = jobIds;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getCycStartDay() {
        return cycStartDay;
    }

    public void setCycStartDay(String cycStartDay) {
        this.cycStartDay = cycStartDay;
    }

    public String getCycEndDay() {
        return cycEndDay;
    }

    public void setCycEndDay(String cycEndDay) {
        this.cycEndDay = cycEndDay;
    }

    public String getBizStartDay() {
        return bizStartDay;
    }

    public void setBizStartDay(String bizStartDay) {
        this.bizStartDay = bizStartDay;
    }

    public String getBizEndDay() {
        return bizEndDay;
    }

    public void setBizEndDay(String bizEndDay) {
        this.bizEndDay = bizEndDay;
    }

    public String getBusinessDateSort() {
        return businessDateSort;
    }

    public void setBusinessDateSort(String businessDateSort) {
        this.businessDateSort = businessDateSort;
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

    public List<Integer> getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(List<Integer> taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public String getExecTimeSort() {
        return execTimeSort;
    }

    public void setExecTimeSort(String execTimeSort) {
        this.execTimeSort = execTimeSort;
    }


    public List<Integer> getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(List<Integer> jobStatuses) {
        this.jobStatuses = jobStatuses;
    }

    public String getTaskNameLike() {
        return taskNameLike;
    }

    public void setTaskNameLike(String taskNameLike) {
        this.taskNameLike = taskNameLike;
    }

    public String getBusinessDateLike() {
        return businessDateLike;
    }

    public void setBusinessDateLike(String businessDateLike) {
        this.businessDateLike = businessDateLike;
    }

    public Long getExecTime() {
        return 0L;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public String getJobNameRightLike() {
        return jobNameRightLike;
    }

    public void setJobNameRightLike(String jobNameRightLike) {
        this.jobNameRightLike = jobNameRightLike;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public Timestamp getStartGmtCreate() {
        return startGmtCreate;
    }

    public void setStartGmtCreate(Timestamp startGmtCreate) {
        this.startGmtCreate = startGmtCreate;
    }

    public Timestamp getEndGmtCreate() {
        return endGmtCreate;
    }

    public void setEndGmtCreate(Timestamp endGmtCreate) {
        this.endGmtCreate = endGmtCreate;
    }

    public Long getTaskCreateId() {
        return taskCreateId;
    }

    public void setTaskCreateId(Long taskCreateId) {
        this.taskCreateId = taskCreateId;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getLikeBusinessDate() {
        return likeBusinessDate;
    }

    public void setLikeBusinessDate(String likeBusinessDate) {
        this.likeBusinessDate = likeBusinessDate;
    }

    public List<Integer> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<Integer> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public Date getExecStartDay() {
        return execStartDay;
    }

    public void setExecStartDay(Date execStartDay) {
        this.execStartDay = execStartDay;
    }

    public Date getExecEndDay() {
        return execEndDay;
    }

    public void setExecEndDay(Date execEndDay) {
        this.execEndDay = execEndDay;
    }
}
