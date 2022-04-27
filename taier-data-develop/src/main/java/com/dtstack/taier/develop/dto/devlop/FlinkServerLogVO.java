package com.dtstack.taier.develop.dto.devlop;

public class FlinkServerLogVO {



    private String jobId;

    /**
     * 提交过程的错误信息
     */
    private String logInfo;

    /**
     * 引擎执行的错误信息
     */
    private String engineLog;

    /**
     * 日志下载
     */
    private String downLoadLog;

    private String submitLog;


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

    public String getDownLoadLog() {
        return downLoadLog;
    }

    public void setDownLoadLog(String downLoadLog) {
        this.downLoadLog = downLoadLog;
    }

    public String getSubmitLog() {
        return submitLog;
    }

    public void setSubmitLog(String submitLog) {
        this.submitLog = submitLog;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public FlinkServerLogVO(String jobId, String logInfo, String engineLog) {
        this.jobId = jobId;
        this.logInfo = logInfo;
        this.engineLog = engineLog;
    }
}
