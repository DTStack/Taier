package com.dtstack.engine.api.vo.action;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 1:59 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ActionRetryLogVO {

    private Integer retryNum;
    private String logInfo;
    private String retryTaskParams;
    private String engineLog;


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

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }
}
