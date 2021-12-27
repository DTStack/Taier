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

import com.dtstack.batch.domain.TenantEngine;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Reason:
 * Date: 2019/6/1
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public interface TenantEngineDao {

    List<TenantEngine> getByTenantId(@Param("tenantId") Long tenantId);

    boolean insert(TenantEngine tenantEngine);

    TenantEngine getByTenantAndEngineType(@Param("tenantId") Long tenantId, @Param("engineType") Integer engineType);

    TenantEngine getDefaultByTenantAndEngineType(@Param("tenantId") Long tenantId, @Param("engineType") Integer engineType);

    List<TenantEngine> listByTenantIds(@Param("tenantIds") Collection<Long> tenantIds);

    List<Integer> getUsedEngineTypeList(@Param("tenantId") Long tenantId);

    TenantEngine getByIdentityAndEngineTypeAndTenantId(@Param("identity") String identity, @Param("engineType") Integer engineType, @Param("tenantId") Long tenantId);

    List<TenantEngine> getByIdentitysAndEngineType(@Param("engineType") Integer engineType, @Param("tenantId") Long tenantId);

    TenantEngine getTenantByDb(@Param("engineIdentity") String engineIdentity, @Param("engineType") Integer engineType, @Param("tenantId") Long tenantId);

    TenantEngine getByTenantIdAndEngineIdentity(@Param("tenantId") Long tenantId, @Param("engineIdentity") String engineIdentity, @Param("engineType") Integer engineType);

    TenantEngine getByTenantIdAndTenantIdAndEngineType(@Param("tenantId") Long tenantId, @Param("engineType") Integer engineType);

    List<TenantEngine> getTenantListByUserId(@Param("engineType") Integer engineType, @Param("tenantIds") Set<Long> tenantIds);

    Integer deleteByTenantId(@Param("tenantId") Long tenantId, @Param("modifyUserId") Long modifyUserId);

    /**
     * 查询项目标识，项目 ID
     * @param tenantIds
     * @param engineType
     * @return
     */
    List<TenantEngine> listIdentityByTenantIdAndType(@Param("tenantIds") List<Long> tenantIds, @Param("engineType") Integer engineType);

}
