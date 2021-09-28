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

package com.dtstack.batch.service.impl;

import com.dtstack.batch.dao.RolePermissionDao;
import com.dtstack.batch.domain.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionService {

    @Autowired
    private RolePermissionDao rolePermissionDao;

    /**
     * 根据 roleId 拿出 关系列表
     * @param roleId
     * @return
     */
    public List<RolePermission> listByRoleId(Long roleId) {
        return rolePermissionDao.listByRoleId(roleId);
    }

    /**
     * 根据roleId  拿出 关联的permissionId
     * @param roleId
     * @return
     */
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        return rolePermissionDao.listPermissionIdsByRoleId(roleId);
    }

    /**
     * 根据roleId permissionids 删除对应记录
     * @param roleId
     * @param existPermissionIds
     * @param userId
     * @return
     */
    public Integer deleteByRoleIdAndPermissionIds(Long roleId, List<Long> existPermissionIds, Long userId) {
        return rolePermissionDao.deleteByRoleIdAndPermissionIds(roleId, existPermissionIds, userId);
    }

    /**
     * 插入
     * @param rolePermission
     * @return
     */
    public Integer insert(RolePermission rolePermission){
        return rolePermissionDao.insert(rolePermission);
    }
}
