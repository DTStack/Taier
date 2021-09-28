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

import com.dtstack.engine.domain.ScheduleJobOperatorRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-07-06
 */
public interface ScheduleJobOperatorRecordDao {

    Long insert(ScheduleJobOperatorRecord engineJobStopRecord);

    Integer delete(@Param("id") Long id);

    Integer deleteByJobIdAndType(@Param("jobId") String jobId,@Param("type")Integer type);

    Integer updateOperatorExpiredVersion(@Param("id") Long id, @Param("operatorExpired") Timestamp operatorExpired, @Param("version") Integer version);

    List<ScheduleJobOperatorRecord> listStopJob(@Param("startId") Long startId);

    List<String> listByJobIds(@Param("jobIds") List<String> jobIds);

    Timestamp getJobCreateTimeById(@Param("id") Long id);

    Long insertBatch(@Param("records") Collection<ScheduleJobOperatorRecord> records);

    List<ScheduleJobOperatorRecord> listJobs(@Param("startId")Long startId, @Param("nodeAddress")String nodeAddress, @Param("type")Integer type);

    void updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("jobIds")List<String> value);
}
