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

import com.dtstack.engine.domain.EngineTenant;
import com.dtstack.engine.common.pager.PageQuery;
import com.dtstack.engine.domain.po.EngineTenantPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EngineTenantDao {

    List<Long> listEngineIdByTenantId(@Param("tenantId") Long tenantId);

    Integer insert(EngineTenant engineTenant);

    Integer updateQueueId(@Param("tenantId") Long tenantId, @Param("engineId") Long engineId, @Param("queueId") Long queueId);

    Integer generalCount(@Param("engineId") Long engineId, @Param("tenantName") String tenantName);

    List<EngineTenantPO> generalQuery(@Param("query") PageQuery<Object> query, @Param("engineId") Long engineId, @Param("tenantName") String tenantName);

    List<EngineTenant> listByEngineIds(@Param("engineIds") List<Long> engineIds);

    List<Long> listTenantIdByQueueIds(@Param("queueIds") List<Long> queueIds);

    Long getQueueIdByTenantId(@Param("tenantId") Long tenantId);

    List<EngineTenantPO> listEngineTenant(Long engineId);

    EngineTenant getByTenantIdAndEngineType(@Param("dtuicTenantId") Long dtuicTenantId, @Param("engineType")Integer engineType);

    Long getClusterIdByTenantId(@Param("dtuicTenantId") Long dtuicTenantId);

    Integer deleteTenantId(@Param("tenantId") Long tenantId);
}

