package com.dtstack.taier.develop.vo.develop.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

public class GetLogsByTaskIdResultVO {

    @ApiModelProperty(value = "任务ID", example = "111")
    private String jobId;

    @ApiModelProperty(value = "提交过程的错误信息", example = "111")
    private String logInfo;

    @ApiModelProperty(value = "引擎执行的错误信息", example = "111")
    private String engineLog;

    @ApiModelProperty(value = "日志下载", example = "111")
    private String downLoadLog;

    @ApiModelProperty(value = "日志下载", example = "111")
    private String submitLog;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
}
