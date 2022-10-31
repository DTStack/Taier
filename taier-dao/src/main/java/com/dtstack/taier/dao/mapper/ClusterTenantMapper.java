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

package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.ClusterTenant;
import com.dtstack.taier.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClusterTenantMapper extends BaseMapper<ClusterTenant> {

    Integer updateQueueName(@Param("tenantId") Long tenantId, @Param("clusterId") Long clusterId, @Param("queueName") String queueName);

    Integer generalCount(@Param("clusterId") Long clusterId, @Param("tenantName") String tenantName);

    List<ClusterTenant> generalQuery(@Param("query") PageQuery<Object> query, @Param("clusterId") Long clusterId, @Param("tenantName") String tenantName);

    String getQueueNameByTenantId(@Param("tenantId") Long tenantId);

    List<ClusterTenant> listByClusterId(@Param("clusterId") Long clusterId);

    Long getClusterIdByTenantId(@Param("tenantId") Long tenantId);

}

