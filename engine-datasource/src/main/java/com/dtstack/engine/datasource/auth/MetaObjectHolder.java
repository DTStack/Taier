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

package com.dtstack.engine.datasource.auth;

import java.util.Optional;

/**
 * @author 猫爸@dtstack.Inc
 * @mission ==> 让数据产生价值
 * @department 产品事业部/技术研发中心
 * @date 2020-03-19 11:41
 */
public class MetaObjectHolder {
    private static ThreadLocal<MetaObject> local = new ThreadLocal<>();


    public static void uid(Long uid) {
        metaObject().setUserId(uid);
    }

    public static void tenantId(Long tenantId) {
        metaObject().setTenantId(tenantId);
    }

    public static void projectId(Long projectId) {
        metaObject().setProjectId(projectId);
    }

    private static MetaObject metaObject() {
        MetaObject object;
        if (local.get() != null) {
            object = local.get();
        } else {
            object = new MetaObject();
            local.set(object);
        }
        return object;
    }

    public static Long uid() {
        return Optional.ofNullable(local.get()).map(MetaObject::getUserId).orElse(0L);
    }

    public static Long tenantId() {
        return Optional.ofNullable(local.get()).map(MetaObject::getTenantId).orElse(0L);
    }

    public static Long projectId() {
        return Optional.ofNullable(local.get()).map(MetaObject::getProjectId).orElse(0L);
    }

    public static void remove() {
        local.remove();
    }

    public static class MetaObject {
        private Long userId;
        private Long tenantId;
        private Long projectId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getTenantId() {
            return tenantId;
        }

        public void setTenantId(Long tenantId) {
            this.tenantId = tenantId;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }
    }
}
