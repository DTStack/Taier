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
import com.dtstack.taier.dao.domain.DevelopFunction;
import com.dtstack.taier.dao.dto.DevelopFunctionDTO;
import com.dtstack.taier.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DevelopFunctionMapper extends BaseMapper<DevelopFunction> {

    /**
     * 根据 租户、父目录id 查询
     * @param tenantId
     * @param nodePid
     * @return
     */
    List<DevelopFunction> listByNodePidAndTenantId(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid);

    /**
     * 根据引擎类型 查询对应函数的跟目录
     * @param taskType
     * @return
     */
    List<DevelopFunction> listSystemFunction(@Param("taskType") Integer taskType);

    /**
     * 根据 租户 查询
     * @param tenantId
     * @param functionType
     * @param taskType
     * @return
     */
    List<DevelopFunction> listTenantFunction(@Param("tenantId") Long tenantId, @Param("functionType") Integer functionType,
                                             @Param("taskType") Integer taskType);

    /**
     * 根据 租户 查询
     * @param tenantId
     * @param taskType
     * @return
     */
    List<DevelopFunction> listTenantByFunction(@Param("tenantId") Long tenantId, @Param("taskType") Integer taskType);


    /**
     * 根据 租户、名称、类型 查询
     * @param tenantId
     * @param name
     * @param type
     * @return
     */
    List<DevelopFunction> listByNameAndTenantId(@Param("tenantId") Long tenantId, @Param("name") String name);

    /**
     * 根据 租户、名称 查询
     * @param tenantId
     * @param name
     * @return
     */
    DevelopFunction getByNameAndTenantId(@Param("tenantId") Long tenantId, @Param("name") String name);

    /**
     * 根据 租户、名称查询
     * @param tenantId
     * @param taskType
     * @return
     */
    List<String> listNameByTenantId(@Param("tenantId") Long tenantId, @Param("taskType") Integer taskType);

    /**
     * 通用查询
     * @param query
     * @return
     */
    List<DevelopFunction> generalQuery(PageQuery<DevelopFunctionDTO> query);

    /**
     * 通用查询统计
     * @param model
     * @return
     */
    Integer generalCount(@Param("model") DevelopFunctionDTO model);

}
