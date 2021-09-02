package com.dtstack.engine.master.jobdealer.bo;

import com.dtstack.engine.common.JobIdentifier;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class JobCompletedInfo implements Delayed {

    private String jobId;
    private JobIdentifier jobIdentifier;
    private String engineType;
    private int computeType;
    private long expired;

    public JobCompletedInfo(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, long delay){
        this.jobId = jobId;
        this.jobIdentifier = jobIdentifier;
        this.engineType = engineType;
        this.computeType = computeType;
        this.expired = System.currentTimeMillis() + delay;
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

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
