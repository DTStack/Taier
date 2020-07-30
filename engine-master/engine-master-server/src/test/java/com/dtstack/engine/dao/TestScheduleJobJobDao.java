/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJobJob;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/7/29
 * Company: www.dtstack.com
 * @author maqi
 */
public interface TestScheduleJobJobDao {

    @Insert({"INSERT INTO schedule_job_job (tenant_id,project_id,dtuic_tenant_id,app_type,job_key,parent_job_key,gmt_create,gmt_modified,is_deleted) \n" +
            "VALUES(#{scheduleJobJob.tenantId}, #{scheduleJobJob.projectId}, #{scheduleJobJob.dtuicTenantId}, #{scheduleJobJob.appType},#{scheduleJobJob.jobKey}," +
            "#{scheduleJobJob.parentJobKey},now(), now(),#{scheduleJobJob.isDeleted})"})
    @Options(useGeneratedKeys = true, keyProperty = "scheduleJobJob.id", keyColumn = "id")
    void insert(@Param("scheduleJobJob") ScheduleJobJob scheduleJobJob);
}
