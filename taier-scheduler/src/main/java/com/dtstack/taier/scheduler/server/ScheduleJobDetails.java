/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.server;

import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 3:00 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobDetails {

    /**
     * 周期实例
     */
    private ScheduleJob scheduleJob;

    /**
     * 周期实例的对应的父实例执行关系
     */
    private List<ScheduleJobJob> jobJobList;

    /**
     * 实例对应的任务信息
     */
    private ScheduleTaskShade scheduleTaskShade;

    /**
     * 如果任务是工作流，那么会生成工作流的子任务
     */
    private List<ScheduleJobDetails> flowBean;

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

    public ScheduleTaskShade getScheduleTaskShade() {
        return scheduleTaskShade;
    }

    public void setScheduleTaskShade(ScheduleTaskShade scheduleTaskShade) {
        this.scheduleTaskShade = scheduleTaskShade;
    }

    public List<ScheduleJobDetails> getFlowBean() {
        return flowBean;
    }

    public void setFlowBean(List<ScheduleJobDetails> flowBean) {
        this.flowBean = flowBean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleJobDetails that = (ScheduleJobDetails) o;
        return Objects.equals(scheduleJob, that.scheduleJob) && Objects.equals(jobJobList, that.jobJobList) && Objects.equals(scheduleTaskShade, that.scheduleTaskShade) && Objects.equals(flowBean, that.flowBean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleJob, jobJobList, scheduleTaskShade, flowBean);
    }

    @Override
    public String toString() {
        return "ScheduleJobDetails{" +
                "scheduleJob=" + scheduleJob +
                ", jobJobList=" + jobJobList +
                ", scheduleTaskShade=" + scheduleTaskShade +
                ", flowBean=" + flowBean +
                '}';
    }
}
