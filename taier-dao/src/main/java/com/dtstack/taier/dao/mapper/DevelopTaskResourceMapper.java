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
import com.dtstack.taier.dao.domain.DevelopTaskResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DevelopTaskResourceMapper extends BaseMapper<DevelopTaskResource> {

    /**
     * @param taskId
     * @param resourceType  资源类型 -- ResourceRefType
     * @return
     */
    List<DevelopTaskResource> listByTaskId(@Param("taskId") Long taskId, @Param("resourceType") Integer resourceType);

    /**
     * 根据 任务Id、资源Id 查询管理关系
     * @param taskId
     * @param resourceId
     * @param resourceType
     * @return
     */
    DevelopTaskResource getByTaskIdAndResourceId(@Param("taskId") Long taskId, @Param("resourceId") Long resourceId, @Param("resourceType") Integer resourceType);

    /**
     * 根据 任务Id、资源类型 查询
     * @param taskId
     * @param resourceType
     * @return
     */
    Integer deleteByTaskId(@Param("taskId") Long taskId, @Param("resourceType") Integer resourceType);

    /**
     * 根据 资源Id 查询
     * @param resourceId
     * @return
     */
    List<DevelopTaskResource> listByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 根据 租户Id 删除
     * @param tenantId
     * @return
     */
    Integer deleteByTenantId(@Param("tenantId") Long tenantId);
}
