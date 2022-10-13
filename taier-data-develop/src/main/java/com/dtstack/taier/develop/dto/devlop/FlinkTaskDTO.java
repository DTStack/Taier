package com.dtstack.taier.develop.dto.devlop;

import java.util.List;

public class FlinkTaskDTO {
    /**
     * 任务id
     */
    private String jobId;

    /**
     * 子dag集合
     */
    private List<TaskVerticesDTO> taskVertices;

    /**
     * 开始时间
     */
    private Long startTime;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<TaskVerticesDTO> getTaskVertices() {
        return taskVertices;
    }

    public void setTaskVertices(List<TaskVerticesDTO> taskVertices) {
        this.taskVertices = taskVertices;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

}
