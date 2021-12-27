package com.dtstack.engine.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @author xuchao
 */

@TableName("schedule_engine_job_cache")
public class EngineJobCache extends BaseEntity{

    private String jobId;

    private String jobInfo;

    private String engineType;

    private Integer computeType;
    private String nodeAddress;
    private String jobName;
    private Integer stage;
    private Long jobPriority;
    private String jobResource;

    private Integer isFailover;

    private String waitReason;

    public String getWaitReason() {
        return waitReason;
    }

    public void setWaitReason(String waitReason) {
        this.waitReason = waitReason;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(Long jobPriority) {
        this.jobPriority = jobPriority;
    }

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public Integer getIsFailover() {
        return isFailover;
    }

    public void setIsFailover(Integer isFailover) {
        this.isFailover = isFailover;
    }
}
