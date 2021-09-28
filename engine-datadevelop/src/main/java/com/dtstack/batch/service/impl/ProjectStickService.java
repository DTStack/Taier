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

import com.dtstack.batch.dao.ProjectStickDao;
import com.dtstack.batch.domain.ProjectStick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectStickService {

    @Autowired
    private ProjectStickDao projectStickDao;

    /**
     * 更新 置顶状态
     * @param projectStick
     * @return
     */
    public Integer updateStick(ProjectStick projectStick) {
        return projectStickDao.updateStick(projectStick);
    }

    /**
     *  新增一条数据
     * @param projectStick
     * @return
     */
    public Integer insert(ProjectStick projectStick) {
        return projectStickDao.insert(projectStick);
    }

    /**
     * 根据 条件获取记录
     * @param userId
     * @param tenantId
     * @param appointProjectId
     * @return
     */
    public ProjectStick getByUserIdAndProjectId(Long userId, Long tenantId, Long appointProjectId) {
        return projectStickDao.getByUserIdAndProjectId(userId, tenantId, appointProjectId);
    }

    /**
     * 根据条件 获取列表
     * @param usefulProjectIds
     * @param userId
     * @param tenantId
     * @return
     */
    public List<ProjectStick> listByProjectIdsAndUserId(List<Long> usefulProjectIds, Long userId, Long tenantId) {
        return projectStickDao.listByProjectIdsAndUserId(usefulProjectIds, userId, tenantId);
    }

    /**
     * 根据projectId 删除记录
     * @param projectId
     * @return
     */
    public Integer deleteByProjectId(Long projectId, Long userId){
        return projectStickDao.deleteByProjectId(projectId, userId);
    }
}
