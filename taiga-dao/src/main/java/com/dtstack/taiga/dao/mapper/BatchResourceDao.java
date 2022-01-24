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

package com.dtstack.taiga.dao.mapper;

import com.dtstack.taiga.dao.domain.BatchResource;
import com.dtstack.taiga.dao.dto.BatchResourceDTO;
import com.dtstack.taiga.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author sishu.yss
 */
public interface BatchResourceDao {

    /**
     * 根据 id 获取资源信息
     * @param id
     * @return
     */
    BatchResource getOne(@Param("id") Long id);

    /**
     * 根据函数找出资源文件地址
     *
     * @param functionId
     * @return
     */
    String getResourceURLByFunctionId(@Param("functionId") Long functionId);


    /**
     * 根据 ids 获取资源列表
     * @param ids
     * @return
     */
    List<BatchResource> listByIds(@Param("ids") List<Long> ids);

    /**
     * 根据 租户、目录Id 获取资源列表
     * @param tenantId
     * @param nodePid
     * @return
     */
    List<BatchResource> listByPidAndTenantId(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid);

    /**
     * 根据 租户 查询资源列表
     * @param tenantId
     * @return
     */
    List<BatchResource> listByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 根据 id 删除数据
     * @param id
     * @return
     */
    Integer deleteById(@Param("id") Long id);

    /**
     * 根据 租户、名称 获取资源列表
     * @param tenantId
     * @param resourceName
     * @return
     */
    List<BatchResource> listByNameAndTenantId(@Param("tenantId") Long tenantId, @Param("resourceName") String resourceName);

    /**
     * 插入数据
     * @param batchResource
     * @return
     */
    Integer insert(BatchResource batchResource);

    /**
     * 更新数据
     * @param batchResource
     * @return
     */
    Integer update(BatchResource batchResource);

    /**
     * 通用查询
     * @param query
     * @return
     */
    List<BatchResource> generalQuery(PageQuery<BatchResourceDTO> query);

    /**
     * 通用查询统计
     * @param model
     * @return
     */
    Integer generalCount(@Param("model") BatchResourceDTO model);

}
