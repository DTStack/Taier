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
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2021-07-26
 */
public interface TestScheduleProjectDao {


    @Insert({" insert into schedule_engine_project ( `project_id`, `uic_tenant_id`," +
            "        `app_type`, `project_name`, `project_alias`," +
            "        `project_Identifier`, `project_desc`, `status`," +
            "        `create_user_id`, `gmt_modified`,`white_status`) VALUES (#{project.projectId},#{project.uicTenantId},#{project.appType},#{project.projectName}," +
            "#{project.projectAlias},#{project.projectIdentifier},#{project.projectDesc},#{project.status}," +
            "#{project.createUserId},now(),#{project.whiteStatus}" +
            ")"})
    @Options()
    Integer insert(@Param("project") ScheduleEngineProject project);
}
