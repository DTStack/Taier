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

package com.dtstack.batch.service.uic.impl.domain;

import java.util.Set;

public class UICUserVO {
    private Long userId;
    private String userName;
    private String fullName;
    private Boolean isRoot;
    private Long tenantId;
    private String tenantName;
    private Boolean tenantAdmin;
    private Boolean tenantOwner;
    private Long belongUserId;
    private Boolean tenantCreator;
    private String phone;
    private Boolean locked;
    private Set<String> products;

    public UICUserVO() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean isRoot() {
        return this.isRoot;
    }

    public void setRoot(Boolean root) {
        this.isRoot = root;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return this.tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Boolean isTenantAdmin() {
        return this.tenantAdmin;
    }

    public void setTenantAdmin(Boolean tenantAdmin) {
        this.tenantAdmin = tenantAdmin;
    }

    public Boolean isTenantOwner() {
        return this.tenantOwner;
    }

    public void setTenantOwner(Boolean tenantOwner) {
        this.tenantOwner = tenantOwner;
    }

    public Long getBelongUserId() {
        return this.belongUserId;
    }

    public void setBelongUserId(Long belongUserId) {
        this.belongUserId = belongUserId;
    }

    public Boolean isTenantCreator() {
        return this.tenantCreator;
    }

    public void setTenantCreator(Boolean tenantCreator) {
        this.tenantCreator = tenantCreator;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean isLocked() {
        return this.locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Set<String> getProducts() {
        return this.products;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
    }

    public String toString() {
        return "UICUserVO{userId=" + this.userId + ", userName='" + this.userName + '\'' + ", fullName='" + this.fullName + '\'' + ", isRoot=" + this.isRoot + ", tenantId=" + this.tenantId + ", tenantName='" + this.tenantName + '\'' + ", tenantAdmin=" + this.tenantAdmin + ", tenantOwner=" + this.tenantOwner + ", belongUserId=" + this.belongUserId + ", tenantCreator=" + this.tenantCreator + ", phone='" + this.phone + '\'' + ", locked=" + this.locked + ", products=" + this.products + '}';
    }
}