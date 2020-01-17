package com.dtstack.engine.master.vo;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author jiangbo
 */
public class BatchServerLogVO {

    private String name;
    private String logInfo;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private int taskType;
    private int computeType;
    private SyncJobInfo syncJobInfo;
    private String downloadLog;
    private Map<String, String> subNodeDownloadLog;
    //经过几次任务重试
    private Integer pageSize;
    //当前页
    private Integer pageIndex;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Map<String, String> getSubNodeDownloadLog() {
        return subNodeDownloadLog;
    }

    public void setSubNodeDownloadLog(Map<String, String> subNodeDownloadLog) {
        this.subNodeDownloadLog = subNodeDownloadLog;
    }

    public String getDownloadLog() {
        return downloadLog;
    }

    public void setDownloadLog(String downloadLog) {
        this.downloadLog = downloadLog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getComputeType() {
        return computeType;
    }

    public void setComputeType(int computeType) {
        this.computeType = computeType;
    }

    public SyncJobInfo getSyncJobInfo() {
        return syncJobInfo;
    }

    public void setSyncJobInfo(SyncJobInfo syncJobInfo) {
        this.syncJobInfo = syncJobInfo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public static class SyncJobInfo{

        private Integer readNum = 0;

        private Integer writeNum = 0;

        private Float dirtyPercent = 0.0F;

        private Long execTime = 0L;

        public Integer getReadNum() {
            return readNum;
        }

        public void setReadNum(Integer readNum) {
            this.readNum = readNum;
        }

        public Integer getWriteNum() {
            return writeNum;
        }

        public void setWriteNum(Integer writeNum) {
            this.writeNum = writeNum;
        }

        public Float getDirtyPercent() {
            return dirtyPercent;
        }

        public void setDirtyPercent(Float dirtyPercent) {
            this.dirtyPercent = dirtyPercent;
        }

        public Long getExecTime() {
            return execTime;
        }

        public void setExecTime(Long execTime) {
            this.execTime = execTime;
        }
    }

}
