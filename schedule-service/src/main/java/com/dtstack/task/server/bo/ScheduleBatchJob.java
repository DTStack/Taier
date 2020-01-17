package com.dtstack.task.server.bo;

import com.dtstack.task.domain.BatchJob;
import com.dtstack.task.domain.BatchJobJob;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Reason:
 * Date: 2018/1/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ScheduleBatchJob {

    private BatchJob batchJob;

    private List<BatchJobJob> jobJobList = Lists.newArrayList();

    /**
     * 下游的任务上一个周期依赖-->null表示还未从数据库拉取过数据
     */
    private List<BatchJob> dependencyChildPrePeriodList;

    public ScheduleBatchJob(BatchJob batchJob) {
        this.batchJob = batchJob;
    }

    public BatchJob getBatchJob() {
        return batchJob;
    }

    public List<BatchJobJob> getBatchJobJobList() {
        return jobJobList;
    }

    public void setJobJobList(List<BatchJobJob> jobJobList) {
        this.jobJobList = jobJobList;
    }

    public void addBatchJobJob(BatchJobJob batchJobJob) {
        jobJobList.add(batchJobJob);
    }

    public Long getId() {
        return batchJob.getId();
    }

    public Integer getStatus() {
        return batchJob.getStatus();
    }

    public String getJobId() {
        return batchJob.getJobId();
    }

    public String getCycTime() {
        return batchJob.getCycTime();
    }

    public String getNextCycTime() {
        return batchJob.getNextCycTime();
    }

    public Long getTaskId() {
        return batchJob.getTaskId();
    }

    public Integer getAppType() {
        return batchJob.getAppType();
    }

    public Integer getScheduleType() {
        return batchJob.getType();
    }

    public String getJobKey() {
        return batchJob.getJobKey();
    }

    public Integer getIsRestart() {
        return batchJob.getIsRestart();
    }

    public List<BatchJob> getDependencyChildPrePeriodList() {
        return dependencyChildPrePeriodList;
    }

    public void setDependencyChildPrePeriodList(List<BatchJob> dependencyChildPrePeriodList) {
        this.dependencyChildPrePeriodList = dependencyChildPrePeriodList;
    }
}
