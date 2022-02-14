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

package com.dtstack.taier.develop.mapstruct.console;


import com.dtstack.taier.dao.domain.ClusterTenant;
import com.dtstack.taier.dao.domain.Queue;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.develop.vo.console.ClusterTenantVO;
import com.dtstack.taier.develop.vo.console.TenantVO;
import org.apache.commons.lang.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = NumberUtils.class)
public interface TenantTransfer {

    TenantTransfer INSTANCE = Mappers.getMapper(TenantTransfer.class);

    @Mapping(source = "id", target = "tenantId")
    TenantVO toVO(Tenant tenant);

    List<TenantVO> toVOs(List<Tenant> tenants);

    @Mapping(source = "queue.maxCapacity", target = "maxCapacity", defaultExpression = "java(NumberUtils.toDouble(queue.getMaxCapacity(),0) * 100 + \"%\")")
    @Mapping(source = "queue.capacity", target = "minCapacity", defaultExpression = "java(NumberUtils.toDouble(queue.getCapacity(),0) * 100 + \"%\")")
    @Mapping(source = "queue.queueName", target = "queue")
    ClusterTenantVO toClusterTenantVO(ClusterTenant clusterTenant, Queue queue);
}
