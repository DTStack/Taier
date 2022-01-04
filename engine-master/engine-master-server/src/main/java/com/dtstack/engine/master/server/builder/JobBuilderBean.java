package com.dtstack.engine.master.server.builder;

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleJobJob;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 3:00 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobBuilderBean {

    /**
     * 周期实例
     */
    private ScheduleJob scheduleJob;

    /**
     * 周期实例的对应的父实例执行关系
     */
    private List<ScheduleJobJob> jobJobList;

    /**
     * 如果任务是工作流，那么会生成工作流的子任务
     */
    private List<JobBuilderBean> flowBean;

    public ScheduleJob getScheduleJob() {
        return scheduleJob;
    }

    public void setScheduleJob(ScheduleJob scheduleJob) {
        this.scheduleJob = scheduleJob;
    }

    public List<ScheduleJobJob> getJobJobList() {
        return jobJobList;
    }

    public void setJobJobList(List<ScheduleJobJob> jobJobList) {
        this.jobJobList = jobJobList;
    }

    public List<JobBuilderBean> getFlowBean() {
        return flowBean;
    }

    public void setFlowBean(List<JobBuilderBean> flowBean) {
        this.flowBean = flowBean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobBuilderBean that = (JobBuilderBean) o;
        return Objects.equals(scheduleJob, that.scheduleJob) && Objects.equals(jobJobList, that.jobJobList) && Objects.equals(flowBean, that.flowBean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleJob, jobJobList, flowBean);
    }
}
