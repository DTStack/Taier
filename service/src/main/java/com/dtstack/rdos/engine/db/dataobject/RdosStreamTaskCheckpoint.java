package com.dtstack.rdos.engine.db.dataobject;

import java.sql.Timestamp;

/**
 * stream checkpoint 对象
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosStreamTaskCheckpoint extends DataObject {

    private String taskId;

    private String taskEngineId;

    private String checkpoint;

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

    public String getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(String checkpoint) {
        this.checkpoint = checkpoint;
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
}
