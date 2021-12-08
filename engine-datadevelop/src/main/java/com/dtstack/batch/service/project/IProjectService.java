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

package com.dtstack.batch.service.project;

import com.dtstack.batch.vo.ProjectEngineVO;

import java.util.List;

/**
 * 项目相关接口
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IProjectService {

    Integer NORMAL_TABLE = 0;

    /**
     *TODO 是否应该返回更明确的创建信息和失败信息
     * 创建项目
     * @param projectId
     * @param projectName
     * @param projectDesc
     * @param userId
     * @param tenantId
     * @param dtuicTenantId
     * @param projectEngineVO
     * @return
     * @throws Exception
     */
    int createProject(Long projectId, String projectName, String projectDesc, Long userId, Long tenantId,
                      Long dtuicTenantId, ProjectEngineVO projectEngineVO) throws Exception;

    /**
     * 获取已经存在的database
     * @return
     */
    List<String> getRetainDB(Long dtuicTenantId,Long userId) throws Exception;

    List<String> getDBTableList(Long dtuicTenantId, Long userId, String dbName, Long projectId) throws Exception;
}
