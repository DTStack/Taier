package com.dtstack.engine.master.vo;

import com.dtstack.engine.api.domain.ScheduleEngineJob;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import org.apache.commons.lang3.StringUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/6
 */
public class ScheduleJobVO extends com.dtstack.engine.api.vo.ScheduleJobVO {

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
            }
        }
        this.batchEngineJob = scheduleEngineJob;
    }
}
