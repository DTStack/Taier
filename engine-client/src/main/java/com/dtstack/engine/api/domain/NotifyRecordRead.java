package com.dtstack.engine.api.domain;

public class NotifyRecordRead extends TenantProjectEntity {
    private Integer appType;
    private Long notifyRecordId;
    private Long contentId;
    private Long userId;
    private Integer readStatus;


    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getNotifyRecordId() {
        return notifyRecordId;
    }

    public void setNotifyRecordId(Long notifyRecordId) {
        this.notifyRecordId = notifyRecordId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
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
}