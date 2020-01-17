package com.dtstack.task.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchAlarm extends AppTenantEntity {

    /**
     * 告警名称
     */
    private String name;

    private Long taskId;

    /**
     * 触发条件   0 失败
     */
    private Integer myTrigger;

    /**
     * 告警状态   0 正常 1关闭 2删除
     */
    private Integer status;

    private Integer senderType;

    private Long createUserId;

    private Integer isTaskHolder;

    private String uncompleteTime;

    /**
     * 接收人详细信息
     */
    private String receivers;

    private String webhook;

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getMyTrigger() {
        return myTrigger;
    }

    public void setMyTrigger(Integer myTrigger) {
        this.myTrigger = myTrigger;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSenderType() {
        return senderType;
    }

    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getIsTaskHolder() {
        return isTaskHolder;
    }

    public void setIsTaskHolder(Integer isTaskHolder) {
        this.isTaskHolder = isTaskHolder;
    }

    public String getUncompleteTime() {
        return uncompleteTime;
    }

    public void setUncompleteTime(String uncompleteTime) {
        this.uncompleteTime = uncompleteTime;
    }
}
