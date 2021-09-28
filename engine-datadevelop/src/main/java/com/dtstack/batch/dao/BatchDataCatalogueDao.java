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

import com.dtstack.batch.domain.BatchDataCatalogue;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * 数据类目
 * @author sanyue
 */
public interface BatchDataCatalogueDao {

    BatchDataCatalogue getOne(@Param("id") long id);

    Integer insert(BatchDataCatalogue catalogue);

    Integer update(BatchDataCatalogue catalogue);

    Integer deleteById(@Param("id") Long id, @Param("gmtModified") Timestamp gmtModified);

    List<BatchDataCatalogue> listByTenantIdAndPId(@Param("tenantId") long tenantId, @Param("nodePid") long nodePid);

    List<BatchDataCatalogue> listByTenantId(@Param("tenantId") long tenantId);

    Integer countByNodePid(@Param("nodePid") long nodePid);

    BatchDataCatalogue getByNodeNameAndNodePid(@Param("nodeName") String nodeName, @Param("nodePid") Long nodePid, @Param("tenantId") long tenantId);

    List<BatchDataCatalogue> listByIds(@Param("ids") List<Long> ids, @Param("tenantId") Long tenantId);

    Long getRootIdByTenantId(@Param("tenantId") Long tenantId);

    BatchDataCatalogue getRootByTenantId(@Param("tenantId") Long tenantId);
}
