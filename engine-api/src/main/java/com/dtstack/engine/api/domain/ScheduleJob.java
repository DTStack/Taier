package com.dtstack.engine.api.domain;


import com.dtstack.engine.api.annotation.Unique;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleJob extends AppTenantEntity {

    /**
     * 工作任务id
     */
    @Unique
    @ApiModelProperty(notes = "工作任务id")
    private String jobId;

    @Unique
    private String jobKey;

    private String jobName;

    /**
     * 任务id
     */
    @ApiModelProperty(notes = "任务id")
    private Long taskId;

    /**
     * 发起操作的用户
     */
    @ApiModelProperty(notes = "发起操作的用户")
    private Long createUserId;

    /**
     * 0正常调度 1补数据 2临时运行
     */
    @ApiModelProperty(notes = "0正常调度 1补数据 2临时运行")
    private Integer type;

    /**
     * 业务日期 yyyymmddhhmmss,调度时间-1d
     */
    @ApiModelProperty(notes = "业务日期 yyyymmddhhmmss,调度时间-1d")
    private String businessDate;

    /***
     * 任务调度时间 yyyymmddhhmmss
     */
    @ApiModelProperty(notes = "任务调度时间 yyyymmddhhmmss")
    private String cycTime;

    private Integer isRestart;

    private Integer dependencyType;

    private String flowJobId;

    private Integer versionId;

    private Integer periodType;

    private Integer status;
    private Integer taskType;
    private Long fillId;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private Long execTime;
    private Date submitTime;
    private Integer maxRetryNum;
    private Integer retryNum;
    private String nodeAddress;

    private String nextCycTime;


    private String logInfo;

    private List<Integer> taskTypes;

    /**
     * 执行引擎任务id
     */
    @ApiModelProperty(notes = "执行引擎任务id")
    private String engineJobId;

    private String applicationId;

    private String engineLog;

    private long pluginInfoId;

    private Integer sourceType;

    private String retryTaskParams;

    private Integer computeType;

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }

    public long getPluginInfoId() {
        return pluginInfoId;
    }

    public void setPluginInfoId(long pluginInfoId) {
        this.pluginInfoId = pluginInfoId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public List<Integer> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<Integer> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public Integer getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(Integer dependencyType) {
        this.dependencyType = dependencyType;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }


    public Integer getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(Integer maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getNextCycTime() {
        return nextCycTime;
    }

    public void setNextCycTime(String nextCycTime) {
        this.nextCycTime = nextCycTime;
    }
}
