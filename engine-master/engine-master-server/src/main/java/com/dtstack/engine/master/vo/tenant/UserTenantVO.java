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

package com.dtstack.engine.master.vo.tenant;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 4:25 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UserTenantVO {

    private Long tenantId;

    private String tenantName;

    private String tenantDesc;

    private Boolean current;

    private Boolean lastLogin;

    private Boolean admin;

    private List<TenantAdminVO> adminList;

    private String createTime;

    private Integer otherUserCount;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Boolean lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public List<TenantAdminVO> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<TenantAdminVO> adminList) {
        this.adminList = adminList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getOtherUserCount() {
        return otherUserCount;
    }

    public void setOtherUserCount(Integer otherUserCount) {
        this.otherUserCount = otherUserCount;
    }
}
