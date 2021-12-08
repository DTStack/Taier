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

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.domain.BatchSysParameter;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.batch.domain.BatchTaskVersionDetail;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.task.vo.query.*;
import com.dtstack.batch.web.task.vo.result.*;
import com.dtstack.engine.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.master.vo.ScheduleDetailsVO;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.master.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.master.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TaskMapstructTransfer {

    TaskMapstructTransfer INSTANCE = Mappers.getMapper(TaskMapstructTransfer.class);

    /**
     * TaskResourceParamVO -> TaskResourceParam
     * @param batchTaskResourceParamVO
     * @return
     */
    TaskResourceParam TaskResourceParamVOToTaskResourceParam(BatchTaskResourceParamVO batchTaskResourceParamVO);

    /**
     * BatchTask -> BatchTaskResultVO
     * @param batchTask
     * @return
     */
    BatchTaskResultVO BatchTaskToResultVO(BatchTask batchTask);


    /**
     * List<BatchTask>  ->  List<BatchTaskResultVO>
     * @param batchTaskList
     * @return
     */
    List<BatchTaskResultVO> BatchTaskListToBatchTaskResultVOList(List<BatchTask> batchTaskList);


    /**
     * BatchScheduleTaskVO -> ScheduleTaskVO
     * @param BatchScheduleTaskVO
     * @return
     */
    ScheduleTaskVO BatchScheduleTaskVToScheduleTaskVO(BatchScheduleTaskVO BatchScheduleTaskVO);

    /**
     * ScheduleTaskVO -> BatchScheduleTaskVO
     * @param scheduleTaskVO
     * @return
     */
    BatchScheduleTaskVO ScheduleTaskVToBatchScheduleTaskVO(ScheduleTaskVO scheduleTaskVO);

    /**
     * TaskCatalogueVO -> TaskCatalogueResultVO
     * @param taskCatalogueVO
     * @return
     */
    TaskCatalogueResultVO TaskCatalogueVOToResultVO(TaskCatalogueVO taskCatalogueVO);

    /**
     * BatchTaskBatchVO -> BatchTaskBatchResultVO
     * @param batchTaskBatchVO
     * @return
     */
    BatchTaskBatchResultVO BatchTaskBatchVOToBatchTaskBatchResultVO(BatchTaskBatchVO batchTaskBatchVO);

    /**
     * List<BatchTaskVersionDetail>  -> List<BatchTaskVersionDetailResultVO>
     * @param batchTaskVersionDetailList
     * @return
     */
    List<BatchTaskVersionDetailResultVO> BatchTaskVersionDetailListToResultVOList(List<BatchTaskVersionDetail> batchTaskVersionDetailList);


    /**
     * BatchTaskVersionDetail -> BatchTaskVersionDetailResultVO
     * @param batchTaskVersionDetail
     * @return
     */
    BatchTaskVersionDetailResultVO BatchTaskVersionDetailToResultVO(BatchTaskVersionDetail batchTaskVersionDetail);

    /**
     * TaskCatalogueVO -> TaskCatalogueResultVO
     * @param taskCatalogueVO
     * @return
     */
    TaskCatalogueResultVO TaskCatalogueVOToTaskCatalogueResultVO(TaskCatalogueVO taskCatalogueVO);

    /**
     * ollection<BatchSysParameter>  -> Collection<BatchSysParameterResultVO>
     * @param batchSysParameterCollection
     * @return
     */
    Collection<BatchSysParameterResultVO> BatchSysParameterCollectionToBatchSysParameterResultVOCollection(Collection<BatchSysParameter> batchSysParameterCollection);


    /**
     * ScheduleTaskVO -> BatchScheduleTaskResultVO
     * @param scheduleTaskVO
     * @return
     */
    BatchScheduleTaskResultVO ScheduleTaskVOToBatchScheduleTaskResultVO(ScheduleTaskVO scheduleTaskVO);

    /**
     * List<BatchResource>  -> List<BatchResourceResultVO>
     * @param batchResourceList
     * @return
     */
    List<BatchResourceResultVO> BatchResourceListToBatchResourceResultVOList(List<BatchResource> batchResourceList);

    /**
     * List<BatchTaskRecordVO> -> List<BatchTaskRecordResultVO>
     * @param batchTaskRecordVOList
     * @return
     */
    List<BatchTaskRecordResultVO> BatchTaskRecordVOListToBatchTaskRecordResultVOList(List<BatchTaskRecordVO> batchTaskRecordVOList);


    /**
     * ReadWriteLockVO -> ReadWriteLockResultVO
     * @param readWriteLockVO
     * @return
     */
    @Mapping(source = "isGetLock", target = "getLock")
    ReadWriteLockResultVO readWriteLockVOToReadWriteLockResultVO(ReadWriteLockVO readWriteLockVO);

    /**
     * BatchTaskBatchVO -> BatchTaskGetTaskByIdResultVO
     * @param batchTaskBatchVO
     * @return
     */
    BatchTaskGetTaskByIdResultVO BatchTaskBatchVOToBatchTaskGetTaskByIdResultVO(BatchTaskBatchVO batchTaskBatchVO);

    /**
     * TaskCheckResultVO -> BatchTaskPublishTaskResultVO
     * @param taskCheckResultVO
     * @return
     */
    BatchTaskPublishTaskResultVO TaskCheckResultVOToBatchTaskPublishTaskResultVO(TaskCheckResultVO taskCheckResultVO);

    /**
     * BatchScheduleTaskShadeVO -> ScheduleTaskShadeDTO
     *
     * @param batchScheduleTaskShadeVO
     * @return
     */
    ScheduleTaskShadeDTO BatchScheduleTaskShadeVOToScheduleTaskShadeDTO(BatchScheduleTaskShadeVO batchScheduleTaskShadeVO);

    /**
     * com.dtstack.engine.pager.PageResult<List<BatchTaskShadePageQueryResultVO>> -> com.dtstack.batch.web.pager.PageResult<List<BatchTaskShadePageQueryResultVO>>
     *
     * @param pageResult
     * @return
     */
    com.dtstack.batch.web.pager.PageResult<List<BatchTaskShadePageQueryResultVO>> BatchTaskShadePageQueryResultVOListTOBatchTaskShadePageQueryResultVOList(PageResult<List<BatchTaskShadePageQueryResultVO>> pageResult);

    /**
     * List<ScheduleTaskShadeTypeVO> -> List<ScheduleTaskResultVO>
     * @param scheduleTaskShadeTypeVOS
     * @return
     */
    List<ScheduleTaskShadeResultVO> scheduleTaskShadeTypeVOsToBatchTaskResultVOs(List<ScheduleTaskShadeTypeVO> scheduleTaskShadeTypeVOS);

    /**
     * List<NotDeleteTaskVO> -> List<BatchPreDeleteTaskResultVO>
     * @param notDeleteTaskVOS
     * @return
     */
    List<BatchGetChildTasksResultVO> notDeleteTaskVOsToBatchGetChildTasksResultVOs(List<NotDeleteTaskVO> notDeleteTaskVOS);

    /**
     * List<BatchTaskTaskAddOrUpdateDependencyVO> -> List<BatchTask>
     * @param dependencyVOS
     * @return
     */
    List<BatchTask> batchTaskTaskAddOrUpdateDependencyVOsToBatchTasks(List<BatchTaskTaskAddOrUpdateDependencyVO> dependencyVOS);

    /**
     * ScheduleDetailsVO --> BatchTaskTaskFindTaskRuleTaskResultVO
     * @param scheduleDetailsVO
     * @return
     */
    BatchTaskTaskFindTaskRuleTaskResultVO scheduleDetailsVOToBatchTaskTaskFindTaskRuleTaskResultVO(ScheduleDetailsVO scheduleDetailsVO);

}
