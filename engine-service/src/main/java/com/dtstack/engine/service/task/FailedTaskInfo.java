package com.dtstack.engine.service.task;

import com.dtstack.engine.common.JobIdentifier;

/**
 *
 * Date: 2018/6/11
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FailedTaskInfo {

    private static final Integer MAX_WAIT_LIMIT = 3;

    private Integer currWaitNum = 0;

    private String jobId;

    private JobIdentifier jobIdentifier;

    private String engineType;

    private int computeType;

    private String pluginInfo;

    public FailedTaskInfo(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, String pluginInfo){
        this.jobId = jobId;
        this.jobIdentifier = jobIdentifier;
        this.engineType = engineType;
        this.computeType = computeType;
        this.pluginInfo = pluginInfo;
    }

    public Integer getCurrWaitNum() {
        return currWaitNum;
    }

    public void setCurrWaitNum(Integer currWaitNum) {
        this.currWaitNum = currWaitNum;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public int getComputeType() {
        return computeType;
    }

    public void setComputeType(int computeType) {
        this.computeType = computeType;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    public boolean allowClean(){
        if(currWaitNum >= MAX_WAIT_LIMIT){
            return false;
        }

        return true;
    }

    public void waitClean(){
        currWaitNum++;
    }

}
