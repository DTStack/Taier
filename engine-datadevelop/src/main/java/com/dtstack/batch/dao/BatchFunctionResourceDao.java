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

import com.dtstack.batch.domain.BatchFunctionResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchFunctionResourceDao {

    List<BatchFunctionResource> listByResourceId(@Param("resourceId") Long resourceId);

    void deleteByFunctionId(@Param("functionId") Long functionId);

    void insert(BatchFunctionResource batchFunctionResource);

    BatchFunctionResource getBeanByResourceIdAndFunctionId(@Param("resourceId") Long resourceId, @Param("functionId") Long functionId);

    List<BatchFunctionResource> listByFunctionId(@Param("functionId") Long functionId);

    List<BatchFunctionResource> listByFunctionResourceId(@Param("resource_Id") Long resource_Id);

    void updateByFunctionId(BatchFunctionResource batchFunctionResource);

    Integer deleteByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据函数id获取函数资源关联关系
     *
     * @param functionId
     * @return
     */
    BatchFunctionResource getResourceFunctionByFunctionId(@Param("functionId") Long functionId);
}
