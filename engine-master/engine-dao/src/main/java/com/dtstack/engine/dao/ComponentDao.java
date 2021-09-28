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

import com.dtstack.engine.domain.Component;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComponentDao {

    Component getOne(@Param("id") Long id);

    Integer insert(Component component);

    Integer update(Component component);

    Integer updateMetadata(@Param("engineId") Long engineId, @Param("type") Integer type,@Param("isMetadata") Integer isMetadata);

    List<Component> listByEngineIds(@Param("engineIds") List<Long> engineId,@Param("type") Integer type);

    List<Component> listDefaultByEngineIds(@Param("engineIds") List<Long> engineIdList);

    Component getByEngineIdAndComponentType(@Param("engineId") Long engineId, @Param("type") Integer type);


    Component getByClusterIdAndComponentType(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("componentVersion")String componentVersion,@Param("deployType") Integer deployType);

    Long getClusterIdByComponentId(@Param("componentId") Long componentId);

    void deleteById(@Param("componentId") Long componentId);

    Component getByTenantIdComponentType(@Param("tenantId") Long tenantId,@Param("componentType") Integer componentType);

    List<Component> listByTenantId(@Param("tenantId") Long tenantId);

    Component getNextDefaultComponent(@Param("engineId") Long engineId, @Param("componentTypeCode") Integer componentTypeCode,@Param("currentDeleteId") Long currentDeleteId);

    String getDefaultComponentVersionByClusterAndComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer type);

    String getDefaultComponentVersionByTenantAndComponentType(@Param("tenantId")Long tenantId,@Param("componentType")Integer componentType);

    /**
     * 此接口返回的component_version为schedule_dict的dict_name
     * e.g 1.10 - 110
     */
    List<Component> getComponentVersionByEngineType(@Param("uicTenantId") Long uicTenantId, @Param("componentTypeCode") Integer componentTypeCode);

    String getDefaultVersionDictNameByUicIdAndComponentType(@Param("uicTenantId") Long uicTenantId, @Param("componentTypeCode") Integer componentTypeCode);

    List<Long> allUseUicTenant(@Param("componentId") Long componentId);

    Component getMetadataComponent(@Param("clusterId") Long clusterId);

    int updateDefault(@Param("engineId")Long engineId, @Param("componentType")Integer componentType, @Param("isDefault") boolean isDefault);
}

