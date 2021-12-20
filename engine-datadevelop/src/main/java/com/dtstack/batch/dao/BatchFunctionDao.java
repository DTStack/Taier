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

package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.dto.BatchFunctionDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface BatchFunctionDao {

    /**
     * 根据id查询
     * @param id
     * @return
     */
    BatchFunction getOne(@Param("id") Long id);

    /**
     * 根据 租户、父目录id 查询
     * @param tenantId
     * @param nodePid
     * @return
     */
    List<BatchFunction> listByNodePidAndTenantId(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid);

    /**
     * 根据引擎类型 查询对应函数的跟目录
     * @param engineType
     * @return
     */
    List<BatchFunction> listSystemFunction(@Param("engineType") Integer engineType);

    /**
     * 根据 租户 查询
     * @param tenantId
     * @param functionType
     * @param engineType
     * @return
     */
    List<BatchFunction> listTenantFunction(@Param("tenantId") Long tenantId, @Param("functionType") Integer functionType, @Param("engineType") Integer engineType);

    /**
     * 根据 租户、名称、类型 查询
     * @param tenantId
     * @param name
     * @param type
     * @return
     */
    List<BatchFunction> listByNameAndTenantId(@Param("tenantId") Long tenantId, @Param("name") String name, @Param("type") Integer type);

    /**
     * 根据 租户、名称 查询
     * @param tenantId
     * @param name
     * @return
     */
    BatchFunction getByNameAndTenantId(@Param("tenantId") Long tenantId, @Param("name") String name);

    /**
     * 插入数据
     * @param batchFunction
     * @return
     */
    Integer insert(BatchFunction batchFunction);

    /**
     * 更新数据
     * @param batchFunction
     * @return
     */
    Integer update(BatchFunction batchFunction);

    /**
     * 根据 租户、名称查询
     * @param tenantId
     * @param engineType
     * @return
     */
    List<String> listNameByTenantId(@Param("tenantId") Long tenantId, @Param("engineType") Integer engineType);

    /**
     * 根据 租户、类型 统计
     * @param tenantId
     * @param type
     * @return
     */
    Integer countByTenantIdAndType(@Param("tenantId") Long tenantId, @Param("type") Integer type);

    /**
     * 通用查询
     * @param query
     * @return
     */
    List<BatchFunction> generalQuery(PageQuery<BatchFunctionDTO> query);

    /**
     * 通用查询统计
     * @param model
     * @return
     */
    Integer generalCount(@Param("model") BatchFunctionDTO model);

    /**
     * 根据 租户 删除
     * @param tenantId
     * @param userId
     * @return
     */
    Integer deleteByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
