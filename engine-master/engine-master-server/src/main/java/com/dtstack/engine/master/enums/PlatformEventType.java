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

package com.dtstack.engine.master.enums;

import org.apache.commons.lang3.StringUtils;

public enum PlatformEventType {
    LOG_OUT("登出"),
    MODIFY_INFO("修改用户信息"),
    DELETE_USER("删除用户"),
    CHANGE_TENANT_OWNER("切换租户所有者"),
    DELETE_TENANT("删除租户"),
    ADD_TENANT("新增租户"),
    EDIT_TENANT("编辑租户"),
    ADD_USER("新增用户"),
    GRANT_USER("用户赋予产品权限"),
    TENANT_ADD_USER("租户添加用户"),
    TENANT_REMOVE_USER("租户移除用户"),
    GRANT_ADMIN("用户置为租户管理员");

    private String comment;

    private PlatformEventType(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public static PlatformEventType getByCode(String code){
        if (StringUtils.isEmpty(code)){
            return null;
        }
        for (PlatformEventType et:values()){
            if (et.name().equalsIgnoreCase(code)){
                return et;
            }
        }
        return null;
    }
}