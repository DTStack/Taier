package com.dtstack.engine.api.domain;

import com.dtstack.engine.api.annotation.Unique;
import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;

/**
 * stream checkpoint 对象
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

@ApiModel
public class EngineJobCheckpoint extends DataObject {

    @Unique
    private String taskId;

    private String taskEngineId;

    private String checkpointId;

    private Timestamp checkpointTrigger;

    private String checkpointSavepath;

    private String checkpointCounts;

    private Timestamp triggerStart;

    private Timestamp triggerEnd;

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

    public Timestamp getTriggerStart() {
        return triggerStart;
    }

    public void setTriggerStart(Timestamp triggerStart) {
        this.triggerStart = triggerStart;
    }

    public Timestamp getTriggerEnd() {
        return triggerEnd;
    }

    public void setTriggerEnd(Timestamp triggerEnd) {
        this.triggerEnd = triggerEnd;
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
}
