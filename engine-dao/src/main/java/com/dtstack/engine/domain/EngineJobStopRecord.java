package com.dtstack.engine.domain;

import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * @author toutian
 */
public class EngineJobStopRecord extends DataObject {

    private String taskId;
    private Integer taskType;
    private String engineType;
    private Integer computeType;
    private String groupName;
    private int version;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static EngineJobStopRecord toEntity(Map<String, Object> jsrMap) {
        EngineJobStopRecord jobStopRecord = new EngineJobStopRecord();
        jobStopRecord.setTaskId(MapUtils.getString(jsrMap, "taskId"));
        jobStopRecord.setTaskType(MapUtils.getInteger(jsrMap, "taskType"));
        jobStopRecord.setEngineType(MapUtils.getString(jsrMap, "engineType"));
        jobStopRecord.setComputeType(MapUtils.getInteger(jsrMap, "computeType"));
        jobStopRecord.setGroupName(MapUtils.getString(jsrMap, "groupName"));
        return jobStopRecord;
    }


    @Override
    public String toString() {
        return "EngineJobStopRecord{" +
                "taskId='" + taskId + '\'' +
                ", taskType=" + taskType +
                ", engineType='" + engineType + '\'' +
                ", computeType=" + computeType +
                ", groupName='" + groupName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

}
