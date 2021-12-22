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

import com.dtstack.batch.dao.ProjectEngineDao;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.service.console.TenantService;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.common.enums.MultiEngineType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目关联引擎相关
 * Date: 2019/6/3
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
@Slf4j
public class ProjectEngineService {

    @Autowired
    private ProjectEngineDao projectEngineDao;


    @Autowired
    private TenantService tenantService;



    @Forbidden
    public ProjectEngine getProjectDb(Long projectId, Integer engineType) {
        return projectEngineDao.getByProjectAndEngineType(projectId, engineType);
    }

    @Forbidden
    public ProjectEngine getProjectEngineByTableType(Long projectId, Integer tableType) {
        MultiEngineType multiEngineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType);
        return projectEngineDao.getByProjectAndEngineType(projectId, multiEngineType.getType());
    }

    @Forbidden
    public String getProjectDbByTableType(Long projectId, Integer tableType) {
        ProjectEngine projectEngine = getProjectEngineByTableType(projectId, tableType);
        return projectEngine.getEngineIdentity();
    }

    @Forbidden
    public List<Integer> getUsedEngineTypeList(Long projectId) {
        return projectEngineDao.getUsedEngineTypeList(projectId);
    }


    public void deleteByProjectId(Long projectId, Long userId) {
        projectEngineDao.deleteByProjectId(projectId, userId);
    }

    @Forbidden
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(ProjectEngine projectEngine) {
        return projectEngineDao.insert(projectEngine);
    }


}