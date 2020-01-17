package com.dtstack.task.server.vo;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class RestartJobVO {

    private Long jobId;

    private String jobKey;

    private Integer jobStatus;

    /***
     * 任务调度时间 yyyymmddhhmmss
     */
    private String cycTime;

    private Long taskId;

    private String taskName;

    private Integer taskType;

    private List<RestartJobVO> childs;

    public List<RestartJobVO> getChilds() {
        return childs;
    }

    public void setChilds(List<RestartJobVO> childs) {
        this.childs = childs;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        if(cycTime.length() != 14){
            this.cycTime = cycTime;
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(cycTime.substring(0, 4))
                .append("-")
                .append(cycTime.substring(4, 6))
                .append("-")
                .append(cycTime.substring(6, 8))
                .append(" ")
                .append(cycTime.substring(8, 10))
                .append(":")
                .append(cycTime.substring(10, 12))
                .append(":")
                .append(cycTime.substring(12, 14));
        this.cycTime = sb.toString();

    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }
}
