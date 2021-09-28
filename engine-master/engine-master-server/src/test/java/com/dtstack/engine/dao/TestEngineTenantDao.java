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

import com.dtstack.engine.domain.EngineTenant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestTenantDao
 * @Description 测试console_engine_tenant表
 * @Date 2020/11/24 19:21
 * @Created chener@dtstack.com
 */
public interface TestEngineTenantDao {

    @Insert({"INSERT INTO console_engine_tenant(engine_id,tenant_id,queue_id)VALUES(#{engineTenant.engineId},#{engineTenant.tenantId},#{engineTenant.queueId})"})
    @Options(useGeneratedKeys=true, keyProperty = "engineTenant.id", keyColumn = "id")
    Integer insert(@Param("engineTenant") EngineTenant engineTenant);
}
