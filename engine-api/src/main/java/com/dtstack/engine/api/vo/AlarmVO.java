package com.dtstack.engine.api.vo;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by jiangbo on 2017/5/19 0019.
 */
public class AlarmVO {
    private Long alarmId;
    private String alarmName;
    private Long taskId;
    private String taskName;
    private List<Integer> senderTypes;
    private int senderType;
    private int taskType;
    private List<Receiver> receiveUsers;
    private int myTrigger;
    private int alarmStatus;
    private Timestamp createTime;
    private String createUser;
    private Long createUserId;
    private String uncompleteTime;
    private String webhook;
    private  Integer isTaskHolder;
    private long tenantId;
    private long projectId;

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public List<Receiver> getReceiveUsers() {
        return receiveUsers;
    }

    public void setReceiveUsers(List<Receiver> receiveUsers) {
        this.receiveUsers = receiveUsers;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public int getMyTrigger() {
        return myTrigger;
    }

    public void setMyTrigger(int myTrigger) {
        this.myTrigger = myTrigger;
    }

    public List<Integer> getSenderTypes() {
        return senderTypes;
    }

    public void setSenderTypes(List<Integer> senderTypes) {
        this.senderTypes = senderTypes;
    }
    

    public int getSenderType() {
		return senderType;
	}

	public void setSenderType(int senderType) {
		this.senderType = senderType;
	}


    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getUncompleteTime() {
        return uncompleteTime;
    }

    public void setUncompleteTime(String uncompleteTime) {
        this.uncompleteTime = uncompleteTime;
    }

    public Integer getIsTaskHolder() {
        return isTaskHolder;
    }

    public void setIsTaskHolder(Integer isTaskHolder) {
        this.isTaskHolder = isTaskHolder;
    }

    public static class Receiver {

        private Long userId;
        private String userName;
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
            this.userId = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
