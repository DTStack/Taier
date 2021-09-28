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

import com.dtstack.engine.domain.ScheduleJobFailed;
import com.dtstack.engine.domain.po.JobTopErrorPO;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/8/11 4:05 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobFailedDao {


    List<JobTopErrorPO> listTopError(@Param("appType") Integer appType,
                                     @Param("dtuicTenantId") Long dtuicTenantId,
                                     @Param("projectId") Long projectId,
                                     @Param("timeTo") Timestamp timeTo);

    Integer insertBatch(@Param("scheduleJobFaileds") List<ScheduleJobFailed> scheduleJobFaileds);

    Integer deleteByGmtCreate(@Param("appType") Integer appType,
                              @Param("uicTenantId") Long uicTenantId,
                              @Param("projectId") Long projectId,
                              @Param("toDate") Date toDate);
}
