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


import com.dtstack.engine.domain.EngineJobRetry;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

public interface TestEngineJobRetryDao {
    @Insert({"insert into schedule_engine_job_retry(gmt_create,gmt_modified,job_id," +
            "engine_job_id,status,engine_log,log_info,application_id,retry_num,retry_task_params)\n" +
            "\t   values(now(), now()," +
            "#{engineJobRetry.jobId},#{engineJobRetry.engineJobId}," +
            "#{engineJobRetry.status},#{engineJobRetry.engineLog}," +
            "#{engineJobRetry.logInfo},#{engineJobRetry.applicationId}," +
            "#{engineJobRetry.retryNum},#{engineJobRetry.retryTaskParams})"})
    @Options(useGeneratedKeys=true, keyProperty = "engineJobRetry.id", keyColumn = "id")
    void insert(@Param("engineJobRetry") EngineJobRetry engineJobRetry);
}
