package com.dtstack.engine.api.domain;

public class Component extends BaseEntity {

    private Long engineId;

    private String componentName;

    private Integer componentTypeCode;

    private String componentConfig;

    private Long clusterId;

    private String hadoopVersion;

    private String uploadFileName;

    private String componentTemplate;

    public String getComponentTemplate() {
        return componentTemplate;
    }

    public void setComponentTemplate(String componentTemplate) {
        this.componentTemplate = componentTemplate;
    }

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(String componentConfig) {
        this.componentConfig = componentConfig;
    }
}
