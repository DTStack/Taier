package com.dtstack.batch.vo.schedule;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 9:07 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskVO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 修改时间
     */
    private Timestamp gmtModified;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 调度类型
     */
    private Integer periodType;

    /**
     * 责任人ID
     */
    private Long ownerUserId;

    /**
     * 责任人
     */
    private String ownerUser;

    /**
     * 工作流id
     */
    private Long flowId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(String ownerUser) {
        this.ownerUser = ownerUser;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
}
