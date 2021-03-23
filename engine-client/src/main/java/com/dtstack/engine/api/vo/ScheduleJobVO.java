package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.ScheduleEngineJob;
import com.dtstack.engine.api.domain.TenantProjectEntity;
import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/6
 */
@ApiModel
public class ScheduleJobVO extends TenantProjectEntity {

    protected ScheduleTaskVO batchTask;
    private String tenantName;
    private String projectName;
    private String jobId;
    private String jobKey;
    private String jobName;
    private int status;
    private long taskId;
    private long createUserId;
    private Long ownerUserId;
    private int type;
    private String businessDate;
    private String cycTime;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private String execTime;
    private String execStartDate;
    private String execEndDate;
    private Integer taskPeriodId;
    private Integer taskRule;
    protected String taskPeriodType;

    private List<ScheduleJobVO> jobVOS;
    private List<ScheduleJobVO> taskRuleJobVOS;
    protected ScheduleEngineJob batchEngineJob;

    private ScheduleJobVO subNodes;

    private String flowJobId;

    private Integer retryNum;

    private List<ScheduleJobVO> relatedJobs;
    //增加是否有脏数据标识 1表示有 0 表示无
    private int isDirty;

    private Integer isRestart;
    // 增加是否是组任务（分钟或小时任务） 如果是 前端就不显示某些信息
    protected boolean isGroupTask;

    private Integer version;

    private Integer taskType;

    private Integer appType;

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ScheduleTaskVO getBatchTask() {
        return batchTask;
    }

    public void setBatchTask(ScheduleTaskVO batchTask) {
        this.batchTask = batchTask;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public String getExecStartDate() {
        return execStartDate;
    }

    public void setExecStartDate(String execStartDate) {
        this.execStartDate = execStartDate;
    }

    public String getExecEndDate() {
        return execEndDate;
    }

    public void setExecEndDate(String execEndDate) {
        this.execEndDate = execEndDate;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public Integer getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(Integer taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public String getTaskPeriodType() {
        return taskPeriodType;
    }

    public void setTaskPeriodType(String taskPeriodType) {
        this.taskPeriodType = taskPeriodType;
    }

    public List<ScheduleJobVO> getJobVOS() {
        return jobVOS;
    }

    public void setJobVOS(List<ScheduleJobVO> jobVOS) {
        this.jobVOS = jobVOS;
    }


    public ScheduleJobVO getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(ScheduleJobVO subNodes) {
        this.subNodes = subNodes;
    }

    public ScheduleEngineJob getBatchEngineJob() {
        return batchEngineJob;
    }

    public void setBatchEngineJob(ScheduleEngineJob batchEngineJob) {
        this.batchEngineJob = batchEngineJob;
    }

    public boolean isGroupTask() {
        return isGroupTask;
    }

    public void setGroupTask(boolean groupTask) {
        isGroupTask = groupTask;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public List<ScheduleJobVO> getRelatedJobs() {
        return relatedJobs;
    }

    public void setRelatedJobs(List<ScheduleJobVO> relatedJobs) {
        this.relatedJobs = relatedJobs;
    }


    public int getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(int isDirty) {
        this.isDirty = isDirty;
    }

    public boolean getIsGroupTask() {
        return isGroupTask;
    }

    public void setIsGroupTask(boolean groupTask) {
        isGroupTask = groupTask;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public List<ScheduleJobVO> getTaskRuleJobVOS() {
        return taskRuleJobVOS;
    }

    public void setTaskRuleJobVOS(List<ScheduleJobVO> taskRuleJobVOS) {
        this.taskRuleJobVOS = taskRuleJobVOS;
    }

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
