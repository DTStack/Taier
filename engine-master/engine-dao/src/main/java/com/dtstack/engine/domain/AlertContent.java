package com.dtstack.engine.domain;

import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2021/1/11 8:06 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertContent {

    private Long id;
    private Long tenantId;
    private Long projectId;
    private Integer appType;
    private String content;
    private String sendInfo;
    private Integer alertMessageStatus;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer isDeleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendInfo() {
        return sendInfo;
    }

    public void setSendInfo(String sendInfo) {
        this.sendInfo = sendInfo;
    }

    public Integer getAlertMessageStatus() {
        return alertMessageStatus;
    }

    public void setAlertMessageStatus(Integer alertMessageStatus) {
        this.alertMessageStatus = alertMessageStatus;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
