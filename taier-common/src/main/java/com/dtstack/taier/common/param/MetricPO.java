package com.dtstack.taier.common.param;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 16:28
 * @Description:
 */
public class MetricPO {

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("subtask_index")
    private Integer subtaskIndex;

    @JsonProperty("task_id")
    private String taskId;

    @JsonProperty("operator_id")
    private String operatorId;

    @JsonProperty("operator_subtask_index")
    private String operatorSubtaskIndex;

    @JsonProperty("quantile")
    private String quantile;

    @JsonProperty("source_id")
    private String sourceId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getSubtaskIndex() {
        return subtaskIndex;
    }

    public void setSubtaskIndex(Integer subtaskIndex) {
        this.subtaskIndex = subtaskIndex;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorSubtaskIndex() {
        return operatorSubtaskIndex;
    }

    public void setOperatorSubtaskIndex(String operatorSubtaskIndex) {
        this.operatorSubtaskIndex = operatorSubtaskIndex;
    }

    public String getQuantile() {
        return quantile;
    }

    public void setQuantile(String quantile) {
        this.quantile = quantile;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
