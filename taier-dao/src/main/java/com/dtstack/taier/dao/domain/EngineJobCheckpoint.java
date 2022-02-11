package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * stream checkpoint 对象
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */


@TableName("engine_job_check_point")
public class EngineJobCheckpoint extends BaseEntity {

    private String taskId;

    private String taskEngineId;

    private String checkpointId;

    private Timestamp checkpointTrigger;

    private String checkpointSavepath;

    private String checkpointCounts;

    /**
     * checkpoint的size大小 单位b
     */
    private Long checkpointSize;

    /**
     * checkpoint持续时间 单位ms
     */
    private Long checkpointDuration;

    public Long getCheckpointDuration() {
        return checkpointDuration;
    }

    public void setCheckpointDuration(Long checkpointDuration) {
        this.checkpointDuration = checkpointDuration;
    }

    public Long getCheckpointSize() {
        return checkpointSize;
    }

    public void setCheckpointSize(Long checkpointSize) {
        this.checkpointSize = checkpointSize;
    }

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
}
