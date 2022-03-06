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

package com.dtstack.taier.develop.mapstruct.vo;

import com.dtstack.taier.dao.domain.BatchResource;
import com.dtstack.taier.dao.domain.BatchSysParameter;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.dto.BatchTaskVersionDetailDTO;
import com.dtstack.taier.dao.dto.UserDTO;
import com.dtstack.taier.develop.dto.devlop.*;
import com.dtstack.taier.develop.vo.develop.query.BatchScheduleTaskVO;
import com.dtstack.taier.develop.vo.develop.query.BatchTaskResourceParamVO;
import com.dtstack.taier.develop.vo.develop.query.BatchTaskTaskAddOrUpdateDependencyVO;
import com.dtstack.taier.develop.vo.develop.result.*;
import com.dtstack.taier.scheduler.vo.ScheduleTaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
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
    Task taskVOTOTask(TaskVO taskVO, @MappingTarget Task task);

    /**
     * Task -> BatchTaskResultVO
     * @param task
     * @return
     */
    BatchTaskResultVO BatchTaskToResultVO(Task task);


    /**
     * BatchScheduleTaskVO -> ScheduleTaskVO
     * @param BatchScheduleTaskVO
     * @return
     */
    ScheduleTaskVO BatchScheduleTaskVToScheduleTaskVO(BatchScheduleTaskVO BatchScheduleTaskVO);


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
     * List<BatchResource>  -> List<BatchResourceResultVO>
     * @param batchResourceList
     * @return
     */
    List<BatchResourceResultVO> BatchResourceListToBatchResourceResultVOList(List<BatchResource> batchResourceList);


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


    BatchUserResultVO dtpToResultVO(UserDTO value);

    /**
     * List<TaskGetNotDeleteVO> -> List<BatchPreDeleteTaskResultVO>
     * @param notDeleteTaskVOS
     * @return
     */
    List<BatchGetChildTasksResultVO> notDeleteTaskVOsToBatchGetChildTasksResultVOs(List<TaskGetNotDeleteVO> notDeleteTaskVOS);

    /**
     * List<BatchTaskTaskAddOrUpdateDependencyVO> -> List<Task>
     * @param dependencyVOS
     * @return
     */
    List<Task> batchTaskTaskAddOrUpdateDependencyVOsToBatchTasks(List<BatchTaskTaskAddOrUpdateDependencyVO> dependencyVOS);
    void taskToTaskVO(Task task, @MappingTarget TaskVO taskVO);

    TaskVO TaskResourceParamToTaskVO(TaskResourceParam taskResourceParam);
}
