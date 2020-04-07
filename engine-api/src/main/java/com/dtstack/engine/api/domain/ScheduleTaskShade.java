package com.dtstack.engine.api.domain;


public class ScheduleTaskShade extends ScheduleTask {

    /**
     * 是否发布到了生产环境
     */
    private Long isPublishToProduce;

    private String extraInfo;

    private Long taskId;

    /**
     * batchJob执行的时候的vesion版本
     */
    private Integer versionId;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getIsPublishToProduce() {
        return isPublishToProduce;
    }

    public void setIsPublishToProduce(Long isPublishToProduce) {
        this.isPublishToProduce = isPublishToProduce;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
