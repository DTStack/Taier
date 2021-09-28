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

import com.dtstack.engine.domain.Engine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EngineDao {

    Integer insert(Engine engine);

    Engine getOne(@Param("id") Long id);

    List<Engine> listByClusterId(@Param("clusterId") Long clusterId);

    List<Engine> listByEngineIds(@Param("engineIds") List<Long> engineIds);

    Engine getByClusterIdAndEngineType(@Param("clusterId") Long clusterId, @Param("engineType") Integer engineType);

    Integer update(Engine engine);

    Integer delete(@Param("id") Long id);

    Integer updateSyncTypeByClusterIdAndEngineType(@Param("clusterId") Long clusterId, @Param("engineType") Integer engineType, @Param("syncType")Integer syncType);

    List<Engine> getByDtUicTenantId(@Param("dtuicTenantId") Long dtuicTenantId);


    Engine getEngineByIdsAndType(@Param("engineIds") List<Long> engineIds,@Param("engineType") Integer engineType);
}
