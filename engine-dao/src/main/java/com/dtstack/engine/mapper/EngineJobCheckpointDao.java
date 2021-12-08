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

package com.dtstack.engine.mapper;

import com.dtstack.engine.domain.EngineJobCheckpoint;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobCheckpointDao {

    int insert(@Param("taskId")String taskId, @Param("engineTaskId")String engineTaskId,
               @Param("checkpointId") String checkpointId,
               @Param("checkpointTrigger") Timestamp checkpointTrigger,
               @Param("checkpointSavepath") String checkpointSavepath,
               @Param("checkpointCounts") String checkpointCounts);

    List<EngineJobCheckpoint> listByTaskIdAndRangeTime(@Param("taskId") String taskEngineId,
                                                       @Param("triggerStart") Long triggerStart,
                                                       @Param("triggerEnd") Long triggerEnd);

    List<EngineJobCheckpoint> listFailedByTaskIdAndRangeTime(@Param("taskId") String taskEngineId,
                                                             @Param("triggerStart") Long triggerStart,
                                                             @Param("triggerEnd") Long triggerEnd, @Param("size") Integer size);

    void updateFailedCheckpoint(@Param("checkPointList") List<EngineJobCheckpoint> checkPointList);

    EngineJobCheckpoint findLatestSavepointByTaskId(@Param("taskId") String taskEngineId);

    EngineJobCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("taskEngineId") String taskEngineId);

    void batchDeleteByEngineTaskIdAndCheckpointId(@Param("taskEngineId") String taskEngineId, @Param("checkpointId") String checkpointId);

    List<EngineJobCheckpoint> getByTaskEngineIdAndCheckpointIndexAndCount(@Param("taskEngineId") String taskEngineId,
                                                                          @Param("taskId") String taskId,
                                                                          @Param("startIndex") int startIndex,
                                                                          @Param("count") int count);

    void cleanAllCheckpointByTaskEngineId(@Param("taskEngineId")  String taskEngineId);

    Integer updateCheckpoint(@Param("taskId") String taskId, @Param("checkpoint") String checkpoint);

    EngineJobCheckpoint getByTaskId(@Param("taskId") String taskId);

    Integer deleteByTaskId(@Param("taskId") String taskId);
}
