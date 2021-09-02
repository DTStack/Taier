package com.dtstack.engine.api.domain;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2021/1/11 9:13 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertRecord {
    private Long id;
    private Long alertChannelId;
    private Integer alertGateType;
    private Long alertContentId;
    private Long tenantId;
    private Integer appType;
    private Long userId;
    private Integer readStatus;
    private String title;
    private Integer status;
    private String context;
    private String jobId;
    private Long readId;
    private Integer alertRecordStatus;
    private Integer alertRecordSendStatus;
    private String failureReason;
    private Integer isDeleted;
    private String nodeAddress;
    private String sendTime;
    private String sendEndTime;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlertChannelId() {
        return alertChannelId;
    }

    public void setAlertChannelId(Long alertChannelId) {
        this.alertChannelId = alertChannelId;
    }

    public Integer getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(Integer alertGateType) {
        this.alertGateType = alertGateType;
    }

    public Long getAlertContentId() {
        return alertContentId;
    }

    public void setAlertContentId(Long alertContentId) {
        this.alertContentId = alertContentId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getAlertRecordStatus() {
        return alertRecordStatus;
    }

    public void setAlertRecordStatus(Integer alertRecordStatus) {
        this.alertRecordStatus = alertRecordStatus;
    }

    public Integer getAlertRecordSendStatus() {
        return alertRecordSendStatus;
    }

    public void setAlertRecordSendStatus(Integer alertRecordSendStatus) {
        this.alertRecordSendStatus = alertRecordSendStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendEndTime() {
        return sendEndTime;
    }

    public void setSendEndTime(String sendEndTime) {
        this.sendEndTime = sendEndTime;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }
}
