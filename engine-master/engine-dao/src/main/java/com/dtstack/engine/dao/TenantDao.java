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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Tenant;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface TenantDao {

    Integer insert(Tenant tenant);

    List<Tenant> listAllDtUicTenantIds();

    String getNameByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    Long getIdByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    Tenant getByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    List<Long> listDtUicTenantIdByIds(@Param("ids") List<Long> ids);

    List<Tenant> listAllTenantByDtUicTenantIds(@Param("ids") List<Long> ids);

    void delete(@Param("dtUicTenantId") Long dtUicTenantId);

    void updateByDtUicTenantId(Tenant tenant);

    Tenant getOne(@Param("id") Long id);

    List<Long> getDtUicTenantIdListByIds(@Param("ids") List<Long> tenantIds);

    List<Tenant> getByDtUicTenantIds(@Param("dtUicTenantIds") Collection<Long> dtUicTenantIds);

    /**
     * 根据 ids 查找租户信息
     *
     * @param ids
     * @return
     */
    List<Tenant> listDtuicTenantIdByTenantId(@Param("ids")List<Long> ids);
}
