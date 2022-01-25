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

package com.dtstack.taiga.develop.mapstruct.vo;

import com.dtstack.taiga.dao.domain.BatchResource;
import com.dtstack.taiga.dao.domain.BatchSysParameter;
import com.dtstack.taiga.dao.domain.BatchTask;
import com.dtstack.taiga.dao.dto.BatchTaskVersionDetailDTO;
import com.dtstack.taiga.develop.vo.BatchTaskBatchVO;
import com.dtstack.taiga.develop.vo.ReadWriteLockVO;
import com.dtstack.taiga.develop.vo.TaskCatalogueVO;
import com.dtstack.taiga.develop.vo.TaskCheckResultVO;
import com.dtstack.taiga.develop.vo.TaskGetNotDeleteVO;
import com.dtstack.taiga.develop.vo.TaskResourceParam;
import com.dtstack.taiga.develop.web.task.vo.query.BatchScheduleTaskResultVO;
import com.dtstack.taiga.develop.web.task.vo.query.BatchScheduleTaskVO;
import com.dtstack.taiga.develop.web.task.vo.query.BatchTaskResourceParamVO;
import com.dtstack.taiga.develop.web.task.vo.query.BatchTaskTaskAddOrUpdateDependencyVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchGetChildTasksResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchResourceResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchSysParameterResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchTaskGetTaskByIdResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchTaskPublishTaskResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchTaskResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.BatchTaskVersionDetailResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.ReadWriteLockResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.ScheduleTaskShadeResultVO;
import com.dtstack.taiga.develop.web.task.vo.result.TaskCatalogueResultVO;
import com.dtstack.taiga.scheduler.vo.ScheduleTaskVO;
import com.dtstack.taiga.scheduler.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
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
     * List<BatchTaskVersionDetail>  -> List<BatchTaskVersionDetailResultVO>
     * @param batchTaskVersionDetailList
     * @return
     */
    List<BatchTaskVersionDetailResultVO> BatchTaskVersionDetailListToResultVOList(List<BatchTaskVersionDetailDTO> batchTaskVersionDetailList);


    /**
     * BatchTaskVersionDetail -> BatchTaskVersionDetailResultVO
     * @param batchTaskVersionDetail
     * @return
     */
    BatchTaskVersionDetailResultVO BatchTaskVersionDetailToResultVO(BatchTaskVersionDetailDTO batchTaskVersionDetail);


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
     * List<ScheduleTaskShadeTypeVO> -> List<ScheduleTaskResultVO>
     * @param scheduleTaskShadeTypeVOS
     * @return
     */
    List<ScheduleTaskShadeResultVO> scheduleTaskShadeTypeVOsToBatchTaskResultVOs(List<ScheduleTaskShadeTypeVO> scheduleTaskShadeTypeVOS);

    /**
     * List<TaskGetNotDeleteVO> -> List<BatchPreDeleteTaskResultVO>
     * @param notDeleteTaskVOS
     * @return
     */
    List<BatchGetChildTasksResultVO> notDeleteTaskVOsToBatchGetChildTasksResultVOs(List<TaskGetNotDeleteVO> notDeleteTaskVOS);

    /**
     * List<BatchTaskTaskAddOrUpdateDependencyVO> -> List<BatchTask>
     * @param dependencyVOS
     * @return
     */
    List<BatchTask> batchTaskTaskAddOrUpdateDependencyVOsToBatchTasks(List<BatchTaskTaskAddOrUpdateDependencyVO> dependencyVOS);

}
