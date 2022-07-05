package com.dtstack.taier.scheduler.jobdealer.bo;

import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.scheduler.enums.EJobLogType;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class JobLogInfo implements Delayed {

    private String jobId;
    private JobIdentifier jobIdentifier;
    private int computeType;
    private long expired;
    private String customLog;
    private EJobLogType logType;

    public JobLogInfo(String jobId, JobIdentifier jobIdentifier, int computeType, long delay, EJobLogType logType){
        this.jobId = jobId;
        this.jobIdentifier = jobIdentifier;
        this.computeType = computeType;
        this.expired = System.currentTimeMillis() + delay;
        this.logType = logType;
    }

    public JobLogInfo() {
    }

    public EJobLogType getLogType() {
        return logType;
    }

    public String getCustomLog() {
        return customLog;
    }

    public void setCustomLog(String customLog) {
        this.customLog = customLog;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
