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
import com.dtstack.taier.dao.domain.BatchFunctionResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 函数 和 资源 的关联关系
 */
public interface DevelopFunctionResourceMapper extends BaseMapper<BatchFunctionResource> {

    /**
     * 根据 资源id 获取函数和资源的关联关系
     * @param resourceId
     * @return
     */
    List<BatchFunctionResource> listByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 根据 函数id 删除函数和资源的关联关系
     * @param functionId
     * @return
     */
    int deleteByFunctionId(@Param("functionId") Long functionId);

    /**
     * 根据 函数id 查询函数和资源的关联关系的列表
     * @param functionId
     * @return
     */
    List<BatchFunctionResource> listByFunctionId(@Param("functionId") Long functionId);

    /**
     * 根据 资源id 查询函数和资源关联关系的列表
     * @param resource_Id
     * @return
     */
    List<BatchFunctionResource> listByFunctionResourceId(@Param("resource_Id") Long resource_Id);

    /**
     * 根据函数id获取函数资源关联关系
     *
     * @param functionId
     * @return
     */
    BatchFunctionResource getResourceFunctionByFunctionId(@Param("functionId") Long functionId);
}
