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
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestTenantDao
 * @Description TODO
 * @Date 2020/11/25 14:04
 * @Created chener@dtstack.com
 */
public interface TestTenantDao {

    @Insert({"INSERT INTO console_dtuic_tenant(id,dt_uic_tenant_id,tenant_name,tenant_desc)VALUES(#{tenant.id},#{tenant.dtUicTenantId},#{tenant.tenantName},#{tenant.tenantDesc})"})
    @Options(useGeneratedKeys=true, keyProperty = "tenant.id", keyColumn = "id")
    Integer insert(@Param("tenant") Tenant tenant);

}
