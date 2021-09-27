package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;

@ApiModel
public class ComponentUser extends BaseEntity{

    private Long clusterId;
    private Integer componentTypeCode;
    private String userName;
    private String password;
    private Boolean isDefault;

    private String label;

    private String labelIp;

    public String getLabelIp() {
        return labelIp;
    }

    public void setLabelIp(String labelIp) {
        this.labelIp = labelIp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
