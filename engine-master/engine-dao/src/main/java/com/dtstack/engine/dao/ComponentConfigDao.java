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

import com.dtstack.engine.domain.ComponentConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-02-08
 */
public interface ComponentConfigDao {

    List<ComponentConfig> listByComponentId(@Param("componentId")Long componentId,@Param("isFilter") boolean isFilter);

    List<ComponentConfig> listByClusterId(@Param("clusterId")Long clusterId,@Param("isFilter") boolean isFilter);

    List<ComponentConfig> listByComponentTypeAndKey(@Param("clusterId")Long clusterId,@Param("key")String key,@Param("componentTypeCode")Integer componentTypeCode);

    ComponentConfig listByKey(@Param("componentId") Long componentId,@Param("key")String key);

    Integer insertBatch(@Param("componentConfigs") List<ComponentConfig> componentConfigs);

    Integer deleteByComponentId(@Param("componentId")Long componentId);

    ComponentConfig listFirst();

    void update(ComponentConfig componentConfig);
}
