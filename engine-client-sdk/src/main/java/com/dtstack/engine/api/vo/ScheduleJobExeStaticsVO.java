package com.dtstack.engine.api.vo;


import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
@ApiModel
public class ScheduleJobExeStaticsVO {

    private int taskType;

    private int cronExeNum;

    private int fillDataExeNum;

    private int failNum;

    private List<BatchJobInfo> jobInfoList = new ArrayList<>();

    public void addBatchJob(BatchJobInfo jobInfo) {
        jobInfoList.add(jobInfo);
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getCronExeNum() {
        return cronExeNum;
    }

    public void setCronExeNum(int cronExeNum) {
        this.cronExeNum = cronExeNum;
    }

    public int getFillDataExeNum() {
        return fillDataExeNum;
    }

    public void setFillDataExeNum(int fillDataExeNum) {
        this.fillDataExeNum = fillDataExeNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public List<BatchJobInfo> getJobInfoList() {
        return jobInfoList;
    }

    public void setJobInfoList(List<BatchJobInfo> jobInfoList) {
        this.jobInfoList = jobInfoList;
    }

    public static class BatchJobInfo {

        private String jobId;

        private Long exeStartTime;

        private Integer exeTime;

        private Integer totalCount;

        private Integer dirtyNum;

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public Long getExeStartTime() {
            return exeStartTime;
        }

        public void setExeStartTime(Long exeStartTime) {
            this.exeStartTime = exeStartTime;
        }

        public Integer getExeTime() {
            return exeTime;
        }

        public void setExeTime(Integer exeTime) {
            this.exeTime = exeTime;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getDirtyNum() {
            return dirtyNum;
        }

        public void setDirtyNum(Integer dirtyNum) {
            this.dirtyNum = dirtyNum;
        }
    }
}
