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

    BatchFunction getOne(@Param("id") Long id);

    List<BatchFunction> listByNodePidAndProjectId(@Param("projectId") Long projectId, @Param("nodePid") Long nodePid);

    List<BatchFunction> listSystemFunction(@Param("engineType") Integer engineType);

    List<BatchFunction> listProjectFunction(@Param("projectId") Long projectId, @Param("functionType") Integer functionType, @Param("engineType") Integer engineType);

    List<BatchFunction> listByNameAndProjectId(@Param("projectId") Long projectId, @Param("name") String name, @Param("type") Integer type);

    BatchFunction getByNameAndProjectId(@Param("projectId") Long projectId, @Param("name") String name);

    Integer insert(BatchFunction batchFunction);

    Integer update(BatchFunction batchFunction);

    List<String> listNameByProjectId(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("engineType") Integer engineType);

    Integer countByProjectIdAndType(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("type") Integer type);

    List<BatchFunction> generalQuery(PageQuery<BatchFunctionDTO> query);

    Integer generalCount(@Param("model") BatchFunctionDTO model);

    List<BatchFunction> listByIds(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("list") List<Long> list, @Param("isDeleted") Integer isDeleted, @Param("type") Integer type);

    List<BatchFunction> listByProjectIdAndType(@Param("projectId") Long projectId, @Param("type") Integer type);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
