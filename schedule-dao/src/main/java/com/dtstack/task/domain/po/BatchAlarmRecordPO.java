package com.dtstack.task.domain.po;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2018/1/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class BatchAlarmRecordPO {

    private Long id;

    private Long alarmId;

    private String alarmContent;

    private Timestamp createTime;

    private Long createUserId;

    private Long taskId;

    private Integer senderType;

    private Integer myTrigger;

    private String receiveUser;

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getSenderType() {
        return senderType;
    }

    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }

    public Integer getMyTrigger() {
        return myTrigger;
    }

    public void setMyTrigger(Integer myTrigger) {
        this.myTrigger = myTrigger;
    }
}
