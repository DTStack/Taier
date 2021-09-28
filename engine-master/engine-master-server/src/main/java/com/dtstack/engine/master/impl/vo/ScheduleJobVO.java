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

package com.dtstack.engine.master.impl.vo;

import com.dtstack.engine.domain.ScheduleEngineJob;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.dtstack.engine.master.server.scheduler.parser.ESchedulePeriodType;
import org.apache.commons.lang3.StringUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/6
 */
public class ScheduleJobVO extends com.dtstack.engine.master.vo.ScheduleJobVO {

    public ScheduleJobVO() {
    }

    public ScheduleJobVO(ScheduleJob scheduleJob) {
        this.setId(scheduleJob.getId());
        this.setJobId(scheduleJob.getJobId());
        this.setJobKey(scheduleJob.getJobKey());
        this.setJobName(scheduleJob.getJobName());
        this.setTaskId(scheduleJob.getTaskId());
        this.setCreateUserId(scheduleJob.getCreateUserId());
        this.setType(scheduleJob.getType());
        this.setGmtCreate(scheduleJob.getGmtCreate());
        this.setGmtModified(scheduleJob.getGmtModified());
        this.setBusinessDate(this.getOnlyDate(scheduleJob.getBusinessDate()));
        this.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
        this.setFlowJobId(scheduleJob.getFlowJobId());
        this.setIsRestart(scheduleJob.getIsRestart());
        this.setTaskPeriodId(scheduleJob.getPeriodType());
        this.setStatus(scheduleJob.getStatus());
        this.setRetryNum(scheduleJob.getRetryNum());
        this.setScheduleEngineJob(new ScheduleEngineJob(scheduleJob));
        this.setExecStartTime(scheduleJob.getExecStartTime());
        this.setExecEndTime(scheduleJob.getExecEndTime());
        this.setTaskRule(scheduleJob.getTaskRule());
        this.setAppType(scheduleJob.getAppType());
        this.setBusinessType(scheduleJob.getBusinessType());
    }

    private String getOnlyDate(String date){
        String str = DateUtil.addTimeSplit(date);
        if (str.length() != 19){
            return str;
        }
        return str.substring(0,11);
    }

    public void setBatchTask(ScheduleTaskVO batchTask) {
        this.isGroupTask = false;
        if (StringUtils.isBlank(taskPeriodType) && null!= getTaskPeriodId()) {
            String taskType = "";
            if (ESchedulePeriodType.MIN.getVal() == getTaskPeriodId()) {
                taskType = "分钟任务";
                this.isGroupTask = true;
            } else if (ESchedulePeriodType.HOUR.getVal() == getTaskPeriodId()) {
                taskType = "小时任务";
                this.isGroupTask = true;
            } else if (ESchedulePeriodType.DAY.getVal() == getTaskPeriodId()) {
                taskType = "天任务";
            } else if (ESchedulePeriodType.WEEK.getVal() == getTaskPeriodId()) {
                taskType = "周任务";
            } else if (ESchedulePeriodType.MONTH.getVal() == getTaskPeriodId()) {
                taskType = "月任务";
            }
            this.taskPeriodType = taskType;
        }
        this.batchTask = batchTask;
    }

    public void setScheduleEngineJob(ScheduleEngineJob scheduleEngineJob) {
        if (scheduleEngineJob != null && null != scheduleEngineJob.getStatus()) {

            //将数据库细分状态归并展示
            this.setStatus(RdosTaskStatus.getShowStatusWithoutStop(scheduleEngineJob.getStatus()));

            int combineStatus = RdosTaskStatus.getShowStatus(scheduleEngineJob.getStatus());
            // 任务状态为运行中，运行完成，运行失败时才有开始时间和运行时间
            if(combineStatus == RdosTaskStatus.RUNNING.getStatus() || combineStatus == RdosTaskStatus.FINISHED.getStatus() || combineStatus == RdosTaskStatus.FAILED.getStatus()){
                if (scheduleEngineJob.getExecStartTime() != null) {
                    this.setExecStartDate(DateUtil.getStandardFormattedDate(scheduleEngineJob.getExecStartTime().getTime()));
                    this.setExecStartTime(scheduleEngineJob.getExecStartTime());
                }

            }

            // 任务状态为运行完成或失败时才有结束时间
            if(combineStatus == RdosTaskStatus.FINISHED.getStatus() || combineStatus == RdosTaskStatus.FAILED.getStatus()){
                if (scheduleEngineJob.getExecEndTime() != null) {
                    this.setExecEndDate(DateUtil.getStandardFormattedDate(scheduleEngineJob.getExecEndTime().getTime()));
                    this.setExecEndTime(scheduleEngineJob.getExecEndTime());
                }
            }

            if (scheduleEngineJob.getExecStartTime() != null && scheduleEngineJob.getExecEndTime() != null) {
                long exeTime = scheduleEngineJob.getExecTime() == null ? 0L : scheduleEngineJob.getExecTime() * 1000;
                this.setExecTime(DateUtil.getTimeDifference(exeTime));
            } else if (scheduleEngineJob.getExecStartTime() != null && scheduleEngineJob.getExecEndTime() == null) {
                long exeTime = scheduleEngineJob.getExecStartTime().getTime();
                long currentTimeMillis = System.currentTimeMillis();
                this.setExecTime(DateUtil.getTimeDifference(currentTimeMillis - exeTime));
            }
        }
        this.batchEngineJob = scheduleEngineJob;
    }
}
