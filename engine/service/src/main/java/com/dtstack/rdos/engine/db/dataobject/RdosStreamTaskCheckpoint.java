package com.dtstack.rdos.engine.db.dataobject;

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

    private Long triggerStart;

    private Long triggerEnd;

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

    public Long getTriggerStart() {
        return triggerStart;
    }

    public void setTriggerStart(Long triggerStart) {
        this.triggerStart = triggerStart;
    }

    public Long getTriggerEnd() {
        return triggerEnd;
    }

    public void setTriggerEnd(Long triggerEnd) {
        this.triggerEnd = triggerEnd;
    }
}
