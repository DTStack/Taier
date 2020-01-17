package com.dtstack.engine.domain.po;

/**
 * @author jiangbo
 * @time 2018/1/11
 */
public class BatchDirtyDataTopPO {

    private String taskName;

    private String tableName;

    private long totalNum;

    private long maxNum;

    private long recentNum;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(long maxNum) {
        this.maxNum = maxNum;
    }

    public long getRecentNum() {
        return recentNum;
    }

    public void setRecentNum(long recentNum) {
        this.recentNum = recentNum;
    }
}
