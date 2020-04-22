package com.dtstack.engine.api.vo;


import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class ScheduleFillDataJobDetailVO {

    private String fillDataJobName;

    private List<FillDataRecord> recordList = new ArrayList<>();

    public void addRecord(FillDataRecord fillDataRecord) {
        this.recordList.add(fillDataRecord);
    }

    public String getFillDataJobName() {
        return fillDataJobName;
    }

    public void setFillDataJobName(String fillDataJobName) {
        this.fillDataJobName = fillDataJobName;
    }

    public List<FillDataRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<FillDataRecord> recordList) {
        this.recordList = recordList;
    }

    public static class FillDataRecord {

        private Long id;

        private String bizDay;

        /**
         * task名称不是实例名称
         */
        private String jobName;

        private Integer taskType;

        private Integer status;

        private String cycTime;

        private String exeStartTime;

        private String exeTime;

        private String dutyUserName;

        private String jobId;

        private String flowJobId;

        private Integer isRestart;

        private Integer retryNum;

        private ScheduleTaskVO batchTask;

        private List<FillDataRecord> relatedRecords;


        private int isDirty;// 脏数据标识

        public FillDataRecord() {
        }

        public FillDataRecord(Long id, String bizDay, String jobName, Integer taskType, Integer status, String cycTime,
                              String exeStartTime, String exeTime, String dutyUserName) {
            this.id = id;
            this.bizDay = bizDay;
            this.jobName = jobName;
            this.taskType = taskType;
            this.status = status;
            this.cycTime = cycTime;
            this.exeStartTime = exeStartTime;
            this.exeTime = exeTime;
            this.dutyUserName = dutyUserName;
        }

        public Integer getIsRestart() {
            return isRestart;
        }

        public void setIsRestart(Integer isRestart) {
            this.isRestart = isRestart;
        }

        public Integer getRetryNum() {
            return retryNum;
        }

        public void setRetryNum(Integer retryNum) {
            this.retryNum = retryNum;
        }

        public String getBizDay() {
            return bizDay;
        }

        public void setBizDay(String bizDay) {
            this.bizDay = bizDay;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getCycTime() {
            return cycTime;
        }

        public void setCycTime(String cycTime) {
            this.cycTime = cycTime;
        }

        public String getExeStartTime() {
            return exeStartTime;
        }

        public void setExeStartTime(String exeStartTime) {
            this.exeStartTime = exeStartTime;
        }

        public String getExeTime() {
            return exeTime;
        }

        public void setExeTime(String exeTime) {
            this.exeTime = exeTime;
        }

        public Integer getTaskType() {
            return taskType;
        }

        public void setTaskType(Integer taskType) {
            this.taskType = taskType;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public ScheduleTaskVO getBatchTask() {
            return batchTask;
        }

        public void setBatchTask(ScheduleTaskVO batchTask) {
            this.batchTask = batchTask;
        }

        public String getDutyUserName() {
            return dutyUserName;
        }

        public void setDutyUserName(String dutyUserName) {
            this.dutyUserName = dutyUserName;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public List<FillDataRecord> getRelatedRecords() {
            return relatedRecords;
        }

        public void setRelatedRecords(List<FillDataRecord> relatedRecords) {
            this.relatedRecords = relatedRecords;
        }

        public String getFlowJobId() {
            return flowJobId;
        }

        public void setFlowJobId(String flowJobId) {
            this.flowJobId = flowJobId;
        }

        public int getIsDirty() {
            return isDirty;
        }

        public void setIsDirty(int isDirty) {
            this.isDirty = isDirty;
        }
    }
}
