package com.dtstack.engine.master.bo;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
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

    private ScheduleJob scheduleJob;

    private List<ScheduleJobJob> jobJobList = Lists.newArrayList();

    /**
     * 下游的任务上一个周期依赖-->null表示还未从数据库拉取过数据
     */
    private List<ScheduleJob> dependencyChildPrePeriodList;

    public ScheduleBatchJob(ScheduleJob scheduleJob) {
        this.scheduleJob = scheduleJob;
    }

    public ScheduleJob getScheduleJob() {
        return scheduleJob;
    }

    public List<ScheduleJobJob> getBatchJobJobList() {
        return jobJobList;
    }

    public void setJobJobList(List<ScheduleJobJob> jobJobList) {
        this.jobJobList = jobJobList;
    }

    public void addBatchJobJob(ScheduleJobJob scheduleJobJob) {
        jobJobList.add(scheduleJobJob);
    }

    public Long getId() {
        return scheduleJob.getId();
    }

    public Integer getStatus() {
        return scheduleJob.getStatus();
    }

    public String getJobId() {
        return scheduleJob.getJobId();
    }

    public String getCycTime() {
        return scheduleJob.getCycTime();
    }

    public String getNextCycTime() {
        return scheduleJob.getNextCycTime();
    }

    public Long getTaskId() {
        return scheduleJob.getTaskId();
    }

    public Integer getAppType() {
        return scheduleJob.getAppType();
    }

    public Integer getScheduleType() {
        return scheduleJob.getType();
    }

    public String getJobKey() {
        return scheduleJob.getJobKey();
    }

    public Integer getIsRestart() {
        return scheduleJob.getIsRestart();
    }

    public List<ScheduleJob> getDependencyChildPrePeriodList() {
        return dependencyChildPrePeriodList;
    }

    public void setDependencyChildPrePeriodList(List<ScheduleJob> dependencyChildPrePeriodList) {
        this.dependencyChildPrePeriodList = dependencyChildPrePeriodList;
    }
}
