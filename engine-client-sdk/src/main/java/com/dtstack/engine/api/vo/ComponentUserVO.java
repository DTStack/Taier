package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.ComponentUser;

import java.util.List;

public class ComponentUserVO {

    private String label;

    private String labelIp;

    private Long clusterId;

    private Integer componentTypeCode;

    private List<ComponentUserInfo> componentUserInfoList;

    private Boolean isDefault;

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


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelIp() {
        return labelIp;
    }

    public void setLabelIp(String labelIp) {
        this.labelIp = labelIp;
    }

    public List<ComponentUserInfo> getComponentUserInfoList() {
        return componentUserInfoList;
    }

    public void setComponentUserInfoList(List<ComponentUserInfo> componentUserInfoList) {
        this.componentUserInfoList = componentUserInfoList;
    }


    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void batchInsert(List<ComponentUser> addComponentUserList) {
    }

    public static class ComponentUserInfo{
        private String userName;
        private String password;

        public ComponentUserInfo(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public ComponentUserInfo() {
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
    }
}
