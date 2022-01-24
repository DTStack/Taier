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

package com.dtstack.taiga.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taiga.dao.domain.EngineJobCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EngineJobCacheMapper extends BaseMapper<EngineJobCache> {

    EngineJobCache getOne(@Param("jobId")String jobId);

    List<String> listNames(@Param("jobName") String jobName);

    List<String> getJobResources();

    List<EngineJobCache> listByJobResource(@Param("jobResource") String jobResource, @Param("stage") Integer stage, @Param("nodeAddress") String nodeAddress, @Param("start") Integer start, @Param("pageSize") Integer pageSize);

    List<Map<String,Object>> groupByJobResourceFilterByCluster(@Param("nodeAddress") String nodeAddress, @Param("clusterName") String clusterName);

    Long countByJobResource(@Param("jobResource") String jobResource, @Param("stage") Integer stage, @Param("nodeAddress") String nodeAddress);

    List<EngineJobCache> listByStage(@Param("startId") Long id, @Param("nodeAddress") String nodeAddress, @Param("stage") Integer stage, @Param("jobResource") String jobResource);

    Long minPriorityByStage(@Param("jobResource") String jobResource, @Param("stages") List<Integer> stages, @Param("nodeAddress") String nodeAddress);

    Integer deleteByJobIds(@Param("jobIds") List<String> jobIds);
}
