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

import com.dtstack.batch.domain.BatchCatalogue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface BatchCatalogueDao {

    BatchCatalogue getOne(@Param("id") long id);

    BatchCatalogue getByPidAndName(@Param("projectId") long projectId, @Param("nodePid") long nodePid, @Param("name") String name);

    List<BatchCatalogue> listByPidAndProjectId(@Param("nodePid") long nodePid, @Param("projectId") long projectId);

    List<BatchCatalogue> listByLevelAndProjectId(@Param("level") Integer level, @Param("projectId") long projectId);

    BatchCatalogue getByLevelAndProjectIdAndName(@Param("level") Integer level, @Param("projectId") long projectId, @Param("name") String name);

    List<BatchCatalogue> listByNameFuzzy(@Param("projectId") long projectId, @Param("name") String name);

    BatchCatalogue getSystemFunctionCatalogueOne(@Param("engineType") int engineType);

    Integer insert(BatchCatalogue batchCatalogue);

    Integer update(BatchCatalogue batchCatalogue);

    List<BatchCatalogue> listByProjectId(@Param("projectId") Long projectId);

    Integer deleteById(@Param("id") long id);

    BatchCatalogue getAllPathParentCatalogues(@Param("nodePid") Long nodePid);

    BatchCatalogue getByLevelAndParentIdAndProjectIdAndName(@Param("level") Integer level, @Param("parentId") Long parentId , @Param("projectId") long projectId, @Param("name") String name);

    Integer getSubAmountsByNodePid(@Param("nodePid") Long nodePid, @Param("projectId") Long projectId);


    List<BatchCatalogue> getListByTenantIdAndCatalogueType(@Param("tenantId") Long tenantId, @Param("catalogueType") Integer catalogueType);

    BatchCatalogue getProjectRoot(@Param("tenantId") Long tenantId, @Param("catalogueType") Integer catalogueType);

    BatchCatalogue getBeanByTenantIdAndNameAndParentId(@Param("tenantId")Long tenantId, @Param("name")String name, @Param("parentId")Long parentId);

    List<BatchCatalogue> listByPidAndNameFuzzy(@Param("projectId") long projectId, @Param("nodePid") long nodePid, @Param("name") String name);
}
