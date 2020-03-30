package com.dtstack.engine.master.vo;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.engine.api.domain.BatchEngineJob;
import com.dtstack.engine.api.domain.BatchJob;
import com.dtstack.engine.master.parser.ESchedulePeriodType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/6
 */
public class BatchJobVO extends com.dtstack.engine.api.vo.BatchJobVO {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobVO.class);

    public BatchJobVO() {
    }

    public BatchJobVO(BatchJob batchJob) {
        this.setId(batchJob.getId());
        this.setJobId(batchJob.getJobId());
        this.setJobKey(batchJob.getJobKey());
        this.setJobName(batchJob.getJobName());
        this.setTaskId(batchJob.getTaskId());
        this.setCreateUserId(batchJob.getCreateUserId());
        this.setType(batchJob.getType());
        this.setGmtCreate(batchJob.getGmtCreate());
        this.setGmtModified(batchJob.getGmtModified());
        this.setBusinessDate(this.getOnlyDate(batchJob.getBusinessDate()));
        this.setCycTime(DateUtil.addTimeSplit(batchJob.getCycTime()));
        this.setFlowJobId(batchJob.getFlowJobId());
        this.setIsRestart(batchJob.getIsRestart());
        this.setTaskPeriodId(batchJob.getPeriodType());
        this.setStatus(batchJob.getStatus());
        this.setRetryNum(batchJob.getRetryNum());
        this.setBatchEngineJob(new BatchEngineJob(batchJob));
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
