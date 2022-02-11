package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 3:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@TableName("schedule_engine_job_retry")
public class ScheduleEngineJobRetry {

    /**
     * 唯一标识
     */
    @TableId(value="id", type= IdType.AUTO)
    private Long id;

    /**
     * 任务状态
     * UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)
     */
    private Integer status;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 执行引擎任务id
     */
    private String engineJobId;

    /**
     * 独立运行的任务需要记录额外的id
     */
    private String applicationId;

    /**
     * 执行开始时间
     */
    private Timestamp execStartTime;

    /**
     * 执行结束时间
     */
    private Timestamp execEndTime;

    /**
     * 执行时，重试的次数
     */
    private Integer retryNum;

    /**
     * 提交日志信息
     */
    private String logInfo;

    /**
     * 引擎日志信息
     */
    private String engineLog;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 最近修的时间
     */
    private Timestamp gmtModified;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

    /**
     * 重试参数
     */
    private String retryTaskParams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

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

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleEngineJobRetry that = (ScheduleEngineJobRetry) o;
        return Objects.equals(id, that.id) && Objects.equals(status, that.status) && Objects.equals(jobId, that.jobId) && Objects.equals(engineJobId, that.engineJobId) && Objects.equals(applicationId, that.applicationId) && Objects.equals(execStartTime, that.execStartTime) && Objects.equals(execEndTime, that.execEndTime) && Objects.equals(retryNum, that.retryNum) && Objects.equals(logInfo, that.logInfo) && Objects.equals(engineLog, that.engineLog) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified) && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(retryTaskParams, that.retryTaskParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, jobId, engineJobId, applicationId, execStartTime, execEndTime, retryNum, logInfo, engineLog, gmtCreate, gmtModified, isDeleted, retryTaskParams);
    }

    @Override
    public String toString() {
        return "ScheduleEngineJobRetry{" +
                "id=" + id +
                ", status=" + status +
                ", jobId='" + jobId + '\'' +
                ", engineJobId='" + engineJobId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", execStartTime=" + execStartTime +
                ", execEndTime=" + execEndTime +
                ", retryNum=" + retryNum +
                ", logInfo='" + logInfo + '\'' +
                ", engineLog='" + engineLog + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted=" + isDeleted +
                ", retryTaskParams='" + retryTaskParams + '\'' +
                '}';
    }
}
