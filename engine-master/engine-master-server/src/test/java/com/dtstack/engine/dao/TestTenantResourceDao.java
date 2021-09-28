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

import com.dtstack.engine.domain.TenantResource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestTenantResourceDao
 * @Description TODO
 * @Date 2020/11/25 15:26
 * @Created chener@dtstack.com
 */
public interface TestTenantResourceDao {

    @Insert({"INSERT INTO console_tenant_resource(tenant_id,dt_uic_tenant_id,task_type,engine_type,resource_limit)VALUES(#{tenantResource.tenantId},#{tenantResource.dtUicTenantId},#{tenantResource.taskType},#{tenantResource.engineType},#{tenantResource.resourceLimit})"})
    @Options(useGeneratedKeys=true, keyProperty = "tenantResource.id", keyColumn = "id")
    Integer insert(@Param("tenantResource") TenantResource tenantResource);

}
