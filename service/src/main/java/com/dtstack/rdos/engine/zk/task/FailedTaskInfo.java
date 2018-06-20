package com.dtstack.rdos.engine.zk.task;

/**
 *
 * Date: 2018/6/11
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FailedTaskInfo {

    private static final Integer TRY_LOG_LIMIT = 3;

    private Integer currLogTry = 0;

    private String jobId;

    private String engineJobId;

    private String engineType;

    private int computeType;

    private String pluginInfo;

    public FailedTaskInfo(String jobId, String engineJobId, String engineType, int computeType, String pluginInfo){
        this.jobId = jobId;
        this.engineJobId = engineJobId;
        this.engineType = engineType;
        this.computeType = computeType;
        this.pluginInfo = pluginInfo;
    }

    public Integer getCurrLogTry() {
        return currLogTry;
    }

    public void setCurrLogTry(Integer currLogTry) {
        this.currLogTry = currLogTry;
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

    public boolean canTryLogAgain(){
        if(currLogTry >= TRY_LOG_LIMIT){
            return false;
        }

        return true;
    }

    public void tryLog(){
        currLogTry++;
    }

}
