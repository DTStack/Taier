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
import com.dtstack.taier.dao.domain.Component;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComponentMapper extends BaseMapper<Component> {

    List<Component> listByClusterId(@Param("clusterId") Long clusterId,@Param("type") Integer typeCode, @Param("isDefault") boolean isDefault);

    Integer updateMetadata(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("isMetadata") Integer isMetadata);

    Component getByClusterIdAndComponentType(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("componentVersion")String componentVersion,@Param("deployType") Integer deployType);

    Component getByVersionName(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("versionName")String versionName,@Param("deployType") Integer deployType);

    Long getClusterIdByComponentId(@Param("componentId") Long componentId);

    String getDefaultComponentVersionByClusterAndComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer type);

    /**
     * 此接口返回的component_version为schedule_dict的dict_name
     * e.g 1.10 - 110
     */
    List<Component> getComponentVersionByEngineType(@Param("tenantId") Long tenantId, @Param("componentTypeCode") Integer componentTypeCode);

    Component getMetadataComponent(@Param("clusterId") Long clusterId);

    int updateDefault(@Param("clusterId")Long clusterId, @Param("componentType")Integer componentType, @Param("isDefault") boolean isDefault);

    List<Component> listByTenantId(@Param("tenantId") Long tenantId);
}

