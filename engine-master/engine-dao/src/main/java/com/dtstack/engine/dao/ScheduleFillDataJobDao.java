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
import com.dtstack.engine.dto.ScheduleJobDTO;
import com.dtstack.engine.common.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleFillDataJobDao {

    ScheduleFillDataJob getByJobName(@Param("jobName") String jobName, @Param("projectId") Long projectId);

    Integer insert(ScheduleFillDataJob fillDataJob);

    List<ScheduleFillDataJob> listFillJob(@Param("nameList") List<String> nameList, @Param("projectId") long projectId);

    List<ScheduleFillDataJob> listFillJobByPageQuery( PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleFillDataJob> getFillJobList(@Param("fillIdList") List<Long> fillIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId,@Param("dtuicTenantId") Long dtuicTenantId);

}
