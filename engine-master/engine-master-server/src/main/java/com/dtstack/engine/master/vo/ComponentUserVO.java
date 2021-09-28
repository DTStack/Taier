/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.vo;

import com.dtstack.engine.domain.ComponentUser;

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
