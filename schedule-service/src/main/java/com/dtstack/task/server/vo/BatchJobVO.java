package com.dtstack.task.server.vo;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.engine.domain.BatchEngineJob;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.task.server.parser.ESchedulePeriodType;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/6
 */
public class BatchJobVO {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobVO.class);

    public BatchJobVO() {
    }

    public BatchJobVO(BatchJob batchJob) {
        this.setId(batchJob.getId());
        this.setJobId(batchJob.getJobId());
        this.setJobKey(batchJob.getJobKey());
        this.setJobName(batchJob.getJobName());
        this.setTaskId(batchJob.getTaskId());
        this.setCreateUserId(batchJob.getCreateUserId());
        this.setType(batchJob.getType());
        this.setGmtCreate(batchJob.getGmtCreate());
        this.setGmtModified(batchJob.getGmtModified());
        this.setBusinessDate(this.getOnlyDate(batchJob.getBusinessDate()));
        this.setCycTime(DateUtil.addTimeSplit(batchJob.getCycTime()));
        this.setFlowJobId(batchJob.getFlowJobId());
        this.setIsRestart(batchJob.getIsRestart());
        this.setTaskPeriodId(batchJob.getPeriodType());
        this.setStatus(batchJob.getStatus());
        this.setRetryNum(batchJob.getRetryNum());
        this.setBatchEngineJob(new BatchEngineJob(batchJob));
    }

    private String getOnlyDate(String date){
        String str = DateUtil.addTimeSplit(date);
        if (str.length() != 19){
            return str;
        }
        return str.substring(0,11);
    }

    private BatchTaskVO batchTask;
    private long id;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
    private int isDeleted;
    private Long tenantId;
    private Long projectId;
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
    private String taskPeriodType;

    private List<BatchJobVO> jobVOS;
    private BatchEngineJob batchEngineJob;

    private BatchJobVO subNodes;

    private String flowJobId;

    private Integer retryNum;

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    private List<BatchJobVO> relatedJobs;
    //增加是否有脏数据标识 1表示有 0 表示无
    private int isDirty;

    private Integer isRestart;
    // 增加是否是组任务（分钟或小时任务） 如果是 前端就不显示某些信息
    private boolean isGroupTask;

    public BatchTaskVO getBatchTask() {
        return batchTask;
    }

    public void setBatchTask(BatchTaskVO batchTask) {
        this.isGroupTask = false;
        if (StringUtil.isBlank(taskPeriodType)) {
            String taskType = "";
            if (ESchedulePeriodType.MIN.getVal() == getTaskPeriodId()) {
                taskType = "分钟任务";
                this.isGroupTask = true;
            } else if (ESchedulePeriodType.HOUR.getVal() == getTaskPeriodId()) {
                taskType = "小时任务";
                this.isGroupTask = true;
            } else if (ESchedulePeriodType.DAY.getVal() == getTaskPeriodId()) {
                taskType = "天任务";
            } else if (ESchedulePeriodType.WEEK.getVal() == getTaskPeriodId()) {
                taskType = "周任务";
            } else if (ESchedulePeriodType.MONTH.getVal() == getTaskPeriodId()) {
                taskType = "月任务";
            }
            this.taskPeriodType = taskType;
        }
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public List<BatchJobVO> getJobVOS() {
        return jobVOS;
    }

    public void setJobVOS(List<BatchJobVO> jobVOS) {
        this.jobVOS = jobVOS;
    }

    public BatchEngineJob getBatchEngineJob() {
        return batchEngineJob;
    }

    public BatchJobVO getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(BatchJobVO subNodes) {
        this.subNodes = subNodes;
    }

    public void setBatchEngineJob(BatchEngineJob batchEngineJob) {
        if (batchEngineJob != null && null != batchEngineJob.getStatus()) {
            this.setStatus(TaskStatusConstrant.getShowStatusWithoutStop(batchEngineJob.getStatus()));

            int combineStatus = TaskStatusConstrant.getShowStatus(batchEngineJob.getStatus());
            // 任务状态为运行中，运行完成，运行失败时才有开始时间和运行时间
            if(combineStatus == TaskStatus.RUNNING.getStatus() || combineStatus == TaskStatus.FINISHED.getStatus() || combineStatus == TaskStatus.FAILED.getStatus()){
                if (batchEngineJob.getExecStartTime() != null) {
                    this.setExecStartDate(DateUtil.getFormattedDate(batchEngineJob.getExecStartTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }

            }

            // 任务状态为运行完成或失败时才有结束时间
            if(combineStatus == TaskStatus.FINISHED.getStatus() || combineStatus == TaskStatus.FAILED.getStatus()){
                if (batchEngineJob.getExecEndTime() != null) {
                    this.setExecEndDate(DateUtil.getFormattedDate(batchEngineJob.getExecEndTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
            if (batchEngineJob.getExecStartTime() != null && batchEngineJob.getExecEndTime() != null) {
                long exeTime = batchEngineJob.getExecTime() == null ? 0L : batchEngineJob.getExecTime() * 1000;
                this.setExecTime(DateUtil.getTimeDifference(exeTime));
            }
        }
        this.batchEngineJob = batchEngineJob;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public List<BatchJobVO> getRelatedJobs() {
        return relatedJobs;
    }

    public void setRelatedJobs(List<BatchJobVO> relatedJobs) {
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
}
