package com.dtstack.engine.domain;


import com.dtstack.engine.common.JobClient;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author toutian
 */
public class RdosEngineJobRetry extends DataObject {

    /**
     * 工作任务id
     */
    private String jobId;

    /**
     * 执行引擎任务id
     */
    private String engineJobId;

    /**
     * 任务状态
     * UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)
     */
    private Byte status;

    private Date execStartTime;

    private Date execEndTime;

    private String logInfo;

    private String engineLog;

    private Integer retryNum;

    private String retryTaskParams;

    private String applicationId;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Date execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Date getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Date execEndTime) {
        this.execEndTime = execEndTime;
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


    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public static RdosEngineJobRetry toEntity(RdosEngineJob batchJob, JobClient jobClient) {
        RdosEngineJobRetry batchJobRetry = new RdosEngineJobRetry();
        batchJobRetry.setJobId(batchJob.getJobId());
        batchJobRetry.setExecStartTime(batchJob.getExecStartTime());
        batchJobRetry.setExecEndTime(batchJob.getExecEndTime());
        batchJobRetry.setRetryNum(batchJob.getRetryNum());
        batchJobRetry.setStatus(batchJob.getStatus());
        batchJobRetry.setGmtCreate(batchJob.getGmtCreate());
        batchJobRetry.setGmtModified(batchJob.getGmtModified());
        batchJobRetry.setEngineLog(batchJob.getEngineLog());
        batchJobRetry.setRetryTaskParams(batchJob.getRetryTaskParams());

        if (batchJob.getApplicationId() == null) {
            batchJobRetry.setApplicationId(jobClient.getApplicationId());
        } else {
            batchJobRetry.setApplicationId(batchJob.getApplicationId());
        }
        if (batchJob.getEngineJobId() == null) {
            batchJobRetry.setEngineJobId(jobClient.getEngineTaskId());
        } else {
            batchJobRetry.setEngineJobId(batchJob.getEngineJobId());
        }
        try {
            if (StringUtils.isEmpty(batchJob.getLogInfo()) && jobClient.getJobResult() != null) {
                batchJobRetry.setLogInfo(jobClient.getJobResult().getMsgInfo());
            } else {
                batchJobRetry.setLogInfo(batchJob.getLogInfo());
            }
        } catch (Throwable e) {
            batchJobRetry.setLogInfo("commit job error，parses log error:" + e.getMessage());
        }
        return batchJobRetry;
    }

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }
}
