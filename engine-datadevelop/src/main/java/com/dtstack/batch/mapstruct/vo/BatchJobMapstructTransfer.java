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

package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.web.job.vo.result.*;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface BatchJobMapstructTransfer {
    BatchJobMapstructTransfer INSTANCE = Mappers.getMapper(BatchJobMapstructTransfer.class);

    /**
     * ExecuteSqlParseVO --> BatchExecuteSqlParseResultVO
     *
     * @param executeSqlParseVO
     * @return
     */
    BatchExecuteSqlParseResultVO executeSqlParseVOToBatchExecuteSqlParseResultVO(ExecuteSqlParseVO executeSqlParseVO);

    /**
     * ExecuteResultVO --> BatchExecuteResultVO
     *
     * @param executeResultVO
     * @return
     */
    BatchExecuteResultVO executeResultVOToBatchExecuteResultVO(ExecuteResultVO executeResultVO);


    /**
     * ScheduleJobExeStaticsVO --> BatchScheduleJobExeStaticsResultVO
     *
     * @param scheduleJobExeStaticsVO
     * @return
     */
    BatchScheduleJobExeStaticsResultVO scheduleJobExeStaticsVOToBatchScheduleJobExeStaticsResultVO(ScheduleJobExeStaticsVO scheduleJobExeStaticsVO);

    /**
     * Map<String, ScheduleJob> --> BMap<String, BatchGetLabTaskRelationMapResultVO>
     *
     * @param scheduleJobMap
     * @return
     */
    Map<String, BatchGetLabTaskRelationMapResultVO> scheduleJobMapToBatchGetLabTaskRelationMapResultVOMap(Map<String, ScheduleJob> scheduleJobMap);

    /**
     * ScheduleDetailsVO --> BatchJobFindTaskRuleJobResultVO
     * @param scheduleDetailsVO
     * @return
     */
    BatchJobFindTaskRuleJobResultVO scheduleDetailsVOToBatchJobFindTaskRuleJobResultVO(ScheduleDetailsVO scheduleDetailsVO);

}
