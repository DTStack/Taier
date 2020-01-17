package com.dtstack.task.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public class Notify extends AppTenantEntity {
    /**
     * 业务类型：1 实时  2 离线
     */
    private Integer bizType;
    /**
     * 关联任务id
     */
    private Long relationId;
    /**
     * 通知名称
     */
    private String name;
    /**
     * 触发类型  0 失败触发... 参照AlarmTrigger类
     */
    private Integer triggerType;
    /**
     * 钉钉告警-》自定义机器人的webhook
     */
    private String webhook;
    /**
     * batch 任务 未完成超时的时间设置,HH:mm
     */
    private String uncompleteTime;
    /**
     * 通知方式，从右到左如果不为0即选中（索引位从0开始，第1位：邮件，第2位: 短信，第3位: 微信，第4位: 钉钉）
     */
    private String sendWay;
    /**
     * 允许通知的开始时间，如5：00，早上5点
     */
    private String startTime;
    /**
     * 允许通知的结束时间，如22：00，不接受告警
     */
    private String endTime;
    /**
     * 0：正常，1：停止 2：删除
     */
    private Integer status;
    /**
     * 创建的用户
     */
    private Long createUserId;

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public String getUncompleteTime() {
        return uncompleteTime;
    }

    public void setUncompleteTime(String uncompleteTime) {
        this.uncompleteTime = uncompleteTime;
    }

    public String getSendWay() {
        return sendWay;
    }

    public void setSendWay(String sendWay) {
        this.sendWay = sendWay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}
