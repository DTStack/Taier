package com.dtstack.engine.api.domain;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2021/1/11 7:17 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertChannel {
    private Long id;
    private Long clusterId;
    private String alertGateName;
    private Integer alertGateType;
    private String alertGateCode;
    private String alertGateJson;
    private String alertGateSource;
    private String alertTemplate;
    private String filePath;
    private Integer isDefault;
    private Integer isDeleted;
    private Timestamp gmtCreated;
    private Timestamp gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getAlertGateName() {
        return alertGateName;
    }

    public void setAlertGateName(String alertGateName) {
        this.alertGateName = alertGateName;
    }

    public Integer getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(Integer alertGateType) {
        this.alertGateType = alertGateType;
    }

    public String getAlertGateCode() {
        return alertGateCode;
    }

    public void setAlertGateCode(String alertGateCode) {
        this.alertGateCode = alertGateCode;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public String getAlertGateSource() {
        return alertGateSource;
    }

    public void setAlertGateSource(String alertGateSource) {
        this.alertGateSource = alertGateSource;
    }

    public String getAlertTemplate() {
        return alertTemplate;
    }

    public void setAlertTemplate(String alertTemplate) {
        this.alertTemplate = alertTemplate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Timestamp gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }
}
