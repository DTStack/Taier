package com.dtstack.engine.master.vo;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.engine.api.domain.BatchEngineJob;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.master.parser.ESchedulePeriodType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/6
 */
public class ScheduleJobVO extends com.dtstack.engine.api.vo.ScheduleJobVO {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleJobVO.class);

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
        this.setBatchEngineJob(new BatchEngineJob(scheduleJob));
    }

    private String getOnlyDate(String date){
        String str = DateUtil.addTimeSplit(date);
        if (str.length() != 19){
            return str;
        }
        return str.substring(0,11);
    }

    public void setBatchTask(BatchTaskVO batchTask) {
        this.isGroupTask = false;
        if (StringUtils.isBlank(taskPeriodType)) {
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

    public void setBatchEngineJob(BatchEngineJob batchEngineJob) {
        if (batchEngineJob != null && null != batchEngineJob.getStatus()) {
            this.setStatus(TaskStatusConstrant.getShowStatusWithoutStop(batchEngineJob.getStatus()));

            int combineStatus = TaskStatusConstrant.getShowStatus(batchEngineJob.getStatus());
            // 任务状态为运行中，运行完成，运行失败时才有开始时间和运行时间
            if(combineStatus == TaskStatus.RUNNING.getStatus() || combineStatus == TaskStatus.FINISHED.getStatus() || combineStatus == TaskStatus.FAILED.getStatus()){
                if (batchEngineJob.getExecStartTime() != null) {
                    this.setExecStartDate(DateUtil.getFormattedDate(batchEngineJob.getExecStartTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }

            }

            // 任务状态为运行完成或失败时才有结束时间
            if(combineStatus == TaskStatus.FINISHED.getStatus() || combineStatus == TaskStatus.FAILED.getStatus()){
                if (batchEngineJob.getExecEndTime() != null) {
                    this.setExecEndDate(DateUtil.getFormattedDate(batchEngineJob.getExecEndTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
            if (batchEngineJob.getExecStartTime() != null && batchEngineJob.getExecEndTime() != null) {
                long exeTime = batchEngineJob.getExecTime() == null ? 0L : batchEngineJob.getExecTime() * 1000;
                this.setExecTime(DateUtil.getTimeDifference(exeTime));
            }
        }
        this.batchEngineJob = batchEngineJob;
    }
}
