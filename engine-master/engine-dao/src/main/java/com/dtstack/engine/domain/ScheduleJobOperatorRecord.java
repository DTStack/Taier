package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;

import java.util.Date;


@ApiModel
public class ScheduleJobOperatorRecord extends DataObject {

    private String jobId;
    private Integer forceCancelFlag;
    private int version;
    private Date operatorExpired;
    private Integer operatorType;
    private String nodeAddress;

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getForceCancelFlag() {
        return forceCancelFlag;
    }

    public void setForceCancelFlag(Integer forceCancelFlag) {
        this.forceCancelFlag = forceCancelFlag;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getOperatorExpired() {
        return operatorExpired;
    }

    public void setOperatorExpired(Date operatorExpired) {
        this.operatorExpired = operatorExpired;
    }


    @Override
    public String toString() {
        return "EngineJobStopRecord{" +
                "jobId='" + jobId + '\'' +
                ", version='" + version + '\'' +
                ", operatorExpired='" + operatorExpired + '\'' +
                '}';
    }

}
