package com.dtstack.taier.develop.dto.devlop;


import java.sql.Timestamp;

public class EngineJobCheckpoint {
    private String taskId;
    private String taskEngineId;
    private String checkpointId;
    private Timestamp checkpointTrigger;
    private String checkpointSavepath;
    private String checkpointCounts;
    private Long checkpointSize;
    private Long checkpointDuration;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskEngineId() {
        return taskEngineId;
    }

    public void setTaskEngineId(String taskEngineId) {
        this.taskEngineId = taskEngineId;
    }

    public String getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(String checkpointId) {
        this.checkpointId = checkpointId;
    }

    public Timestamp getCheckpointTrigger() {
        return checkpointTrigger;
    }

    public void setCheckpointTrigger(Timestamp checkpointTrigger) {
        this.checkpointTrigger = checkpointTrigger;
    }

    public String getCheckpointSavepath() {
        return checkpointSavepath;
    }

    public void setCheckpointSavepath(String checkpointSavepath) {
        this.checkpointSavepath = checkpointSavepath;
    }

    public String getCheckpointCounts() {
        return checkpointCounts;
    }

    public void setCheckpointCounts(String checkpointCounts) {
        this.checkpointCounts = checkpointCounts;
    }

    public Long getCheckpointSize() {
        return checkpointSize;
    }

    public void setCheckpointSize(Long checkpointSize) {
        this.checkpointSize = checkpointSize;
    }

    public Long getCheckpointDuration() {
        return checkpointDuration;
    }

    public void setCheckpointDuration(Long checkpointDuration) {
        this.checkpointDuration = checkpointDuration;
    }



    public EngineJobCheckpoint() {
    }
}
