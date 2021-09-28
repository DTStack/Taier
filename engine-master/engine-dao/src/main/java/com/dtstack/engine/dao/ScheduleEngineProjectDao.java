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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleEngineProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/5 10:47 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleEngineProjectDao {

    Integer insert(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    ScheduleEngineProject getProjectByProjectIdAndApptype(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    Integer updateById(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    Integer deleteByProjectIdAppType(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    List<ScheduleEngineProject> selectFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId,@Param("projectId") Long projectId, @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit);

    List<ScheduleEngineProject> listByProjectIds(@Param("projectIds") List<Long> projectIds, @Param("appType") Integer appType);

    List<ScheduleEngineProject> listWhiteListProject();

    ScheduleEngineProject getByName(@Param("projectName") String projectName, @Param("dtUicTenantId") Long dtUicTenantId);

    List<ScheduleEngineProject> listByTenantIds(@Param("dtUicTenantIds") List<Long> tenantIds);
}
