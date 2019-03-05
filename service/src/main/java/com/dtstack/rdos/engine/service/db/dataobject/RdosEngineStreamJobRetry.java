package com.dtstack.rdos.engine.service.db.dataobject;

import java.util.Date;

/**
 * @author toutian
 */
public class RdosEngineStreamJobRetry extends DataObject {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 计算引擎任务id
     */
    private String engineTaskId;

    private String applicationId;

    /**
     * 任务状态
     */
    private Byte status;

    private Date execStartTime;

    private Date execEndTime;

    private String logInfo;

    private String engineLog;

    private Integer retryNum;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getEngineTaskId() {
        return engineTaskId;
    }

    public void setEngineTaskId(String engineTaskId) {
        this.engineTaskId = engineTaskId;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public static RdosEngineStreamJobRetry toEntity(RdosEngineStreamJob streamJob) {
        RdosEngineStreamJobRetry streamJobRetry = new RdosEngineStreamJobRetry();
        streamJobRetry.setTaskId(streamJob.getTaskId());
        if (streamJob.getApplicationId() == null) {
            streamJobRetry.setApplicationId("");
        } else {
            streamJobRetry.setApplicationId(streamJob.getApplicationId());
        }
        if (streamJob.getEngineTaskId() == null) {
            streamJobRetry.setEngineTaskId("");
        } else {
            streamJobRetry.setEngineTaskId(streamJob.getEngineTaskId());
        }
        if (streamJob.getEngineLog() == null) {
            streamJobRetry.setEngineLog("");
        } else {
            streamJobRetry.setEngineLog(streamJob.getEngineLog());
        }
        if (streamJob.getLogInfo() == null) {
            streamJobRetry.setLogInfo("");
        } else {
            streamJobRetry.setLogInfo(streamJob.getLogInfo());
        }
        streamJobRetry.setExecStartTime(streamJob.getExecStartTime());
        streamJobRetry.setExecEndTime(streamJob.getExecEndTime());
        streamJobRetry.setRetryNum(streamJob.getRetryNum());
        streamJobRetry.setStatus(streamJob.getStatus());
        streamJobRetry.setGmtCreate(streamJob.getGmtCreate());
        streamJobRetry.setGmtModified(streamJob.getGmtModified());
        return streamJobRetry;
    }
}
