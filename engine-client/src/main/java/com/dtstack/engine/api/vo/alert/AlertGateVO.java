package com.dtstack.engine.api.vo.alert;



public class AlertGateVO {

    private Long id;

    private String alertGateSource;

    private String alertGateName;

    private Integer clusterId;

    private Integer isDefault;

    /**
     * <p>
     *     告警通道类型,见{@link}
     * </p>
     */
    private int alertGateType;

    private String alertGateJson;

    private String alertGateCode;
    
    private Long alertTemplateId;

    private String alertTemplate;
    
    private String filePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertGateSource() {
        return alertGateSource;
    }

    public void setAlertGateSource(String alertGateSource) {
        this.alertGateSource = alertGateSource;
    }

    public String getAlertGateName() {
        return alertGateName;
    }

    public void setAlertGateName(String alertGateName) {
        this.alertGateName = alertGateName;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public int getAlertGateType() {
        return alertGateType;
    }

    public void setAlertGateType(int alertGateType) {
        this.alertGateType = alertGateType;
    }

    public String getAlertGateJson() {
        return alertGateJson;
    }

    public void setAlertGateJson(String alertGateJson) {
        this.alertGateJson = alertGateJson;
    }

    public String getAlertGateCode() {
        return alertGateCode;
    }

    public void setAlertGateCode(String alertGateCode) {
        this.alertGateCode = alertGateCode;
    }

    public Long getAlertTemplateId() {
        return alertTemplateId;
    }

    public void setAlertTemplateId(Long alertTemplateId) {
        this.alertTemplateId = alertTemplateId;
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
}
