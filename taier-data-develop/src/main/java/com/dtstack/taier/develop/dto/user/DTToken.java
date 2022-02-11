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

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuebai
 * @date 2021-08-04
 */
public class DTToken implements Serializable {
    /**
     * 用户id
     */
    public static final String USER_ID = "user_id";
    /**
     * 用户名
     */
    public static final String USER_NAME = "user_name";
    /**
     * 租户id
     */
    public static final String TENANT_ID = "tenant_id";
    /**
     * 登录用户id
     */
    private Long userId;
    /**
     * 登录用户名
     */
    private String userName;
    /**
     * 登录租户组id
     */
    private Long tenantId;
    /**
     * 过期时间
     */
    private Date expireAt;

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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }
}