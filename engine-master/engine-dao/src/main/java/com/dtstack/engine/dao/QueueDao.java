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

import com.dtstack.engine.domain.Queue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QueueDao {

    Integer insert(Queue queue);

    List<Queue> listByEngineId(@Param("engineId") Long engineId);

    List<Queue> listByEngineIdWithLeaf(@Param("engineIds") List<Long> engineIds);

    Integer update(Queue oldQueue);

    Queue getOne(@Param("id") Long id);

    Integer deleteByIds(@Param("ids") List<Long> collect, @Param("engineId") Long engineId);

    Integer countByParentQueueId(@Param("parentQueueId") Long parentQueueId);

    List<Queue> listByIds(@Param("ids") List<Long> ids);
}

