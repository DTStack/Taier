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

package com.dtstack.taier.develop.dto.user;

/**
 * @author toutian
 */
public class DtUser {

    private Long userId;

    private String userName;

    private String email;

    private String phone;

    private Long tenantId;

    /**
     * 租户所有者用户id
     */
    private Long tenantOwnerId;

    private String tenantName;

    private Boolean tenantOwner;

    /**
     * 仅标注是否是管理员
     */
    private Boolean isRootOnly;

    /**
     * 仅标注是否是tenant owner
     */
    private Boolean isOwnerOnly;

    public Boolean getTenantOwner() {
        return tenantOwner;
    }

    public void setTenantOwner(Boolean tenantOwner) {
        this.tenantOwner = tenantOwner;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantOwnerId() {
        return tenantOwnerId;
    }

    public void setTenantOwnerId(Long tenantOwnerId) {
        this.tenantOwnerId = tenantOwnerId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Boolean getRootOnly() {
        return isRootOnly;
    }

    public void setRootOnly(Boolean rootOnly) {
        isRootOnly = rootOnly;
    }

    public Boolean getOwnerOnly() {
        return isOwnerOnly;
    }

    public void setOwnerOnly(Boolean ownerOnly) {
        isOwnerOnly = ownerOnly;
    }
}
