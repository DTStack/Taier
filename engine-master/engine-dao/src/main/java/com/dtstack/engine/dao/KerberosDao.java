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

import com.dtstack.engine.domain.KerberosConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KerberosDao {

    KerberosConfig getByComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType,@Param("componentVersion")String componentVersion);

    List<KerberosConfig> getByClusters(@Param("clusterId") Long clusterId);

    List<KerberosConfig> listAll();

    Integer update(KerberosConfig kerberosConfig);

    Integer insert(KerberosConfig kerberosConfig);

    void deleteByComponentId(@Param("componentId") Long componentId);

    void deleteByComponent(@Param("engineId")Long engineId,@Param("componentTypeCode")Integer componentTypeCode,@Param("componentVersion")String componentVersion);


    KerberosConfig getByEngineIdAndComponentType(@Param("engineId") Long engineId, @Param("componentType") Integer componentType);
}
