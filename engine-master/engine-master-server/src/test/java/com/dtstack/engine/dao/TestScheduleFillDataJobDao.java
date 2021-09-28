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

import com.dtstack.engine.domain.ScheduleFillDataJob;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestScheduleFillDataJobDao
 * @Description TODO
 * @Date 2020/11/26 17:04
 * @Created chener@dtstack.com
 */
public interface TestScheduleFillDataJobDao {

    @Insert({"INSERT INTO schedule_fill_data_job(job_name,run_day,from_day,to_day,create_user_id,dtuic_tenant_id,tenant_id,project_id,app_type)VALUES(#{scheduleFillDataJob.jobName},#{scheduleFillDataJob.runDay},#{scheduleFillDataJob.fromDay},#{scheduleFillDataJob.toDay},#{scheduleFillDataJob.createUserId},#{scheduleFillDataJob.dtuicTenantId},#{scheduleFillDataJob.tenantId},#{scheduleFillDataJob.projectId},#{scheduleFillDataJob.appType})"})
    @Options(useGeneratedKeys=true, keyProperty = "scheduleFillDataJob.id", keyColumn = "id")
    Integer insert(@Param("scheduleFillDataJob") ScheduleFillDataJob scheduleFillDataJob);

}
