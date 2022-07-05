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

import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.DevelopSysParameter;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.dto.DevelopTaskVersionDetailDTO;
import com.dtstack.taier.dao.dto.UserDTO;
import com.dtstack.taier.develop.dto.devlop.*;
import com.dtstack.taier.develop.vo.develop.query.DevelopScheduleTaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskResourceParamVO;
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
     * @param DevelopTaskResourceParamVO
     * @return
     */
    TaskResourceParam TaskResourceParamVOToTaskResourceParam(DevelopTaskResourceParamVO DevelopTaskResourceParamVO);
    Task taskVOTOTask(TaskVO taskVO, @MappingTarget Task task);

    /**
     * Task -> DevelopTaskResultVO
     * @param task
     * @return
     */
    DevelopTaskResultVO DevelopTaskToResultVO(Task task);


    /**
     * DevelopScheduleTaskVO -> ScheduleTaskVO
     * @param DevelopScheduleTaskVO
     * @return
     */
    ScheduleTaskVO DevelopScheduleTaskVToScheduleTaskVO(DevelopScheduleTaskVO DevelopScheduleTaskVO);


    /**
     * Task -> TaskListResultVO
     *
     * @param task
     * @return
     */
    TaskListResultVO taskVToTaskListResult(Task task);

    /**
     * TaskCatalogueVO -> TaskCatalogueResultVO
     * @param taskCatalogueVO
     * @return
     */
    TaskCatalogueResultVO TaskCatalogueVOToResultVO(TaskCatalogueVO taskCatalogueVO);


    /**
     * List<DevelopTaskVersionDetail>  -> List<DevelopTaskVersionDetailResultVO>
     * @param DevelopTaskVersionDetailList
     * @return
     */
    List<DevelopTaskVersionDetailResultVO> DevelopTaskVersionDetailListToResultVOList(List<DevelopTaskVersionDetailDTO> DevelopTaskVersionDetailList);


    /**
     * DevelopTaskVersionDetail -> DevelopTaskVersionDetailResultVO
     * @param DevelopTaskVersionDetail
     * @return
     */
    DevelopTaskVersionDetailResultVO DevelopTaskVersionDetailToResultVO(DevelopTaskVersionDetailDTO DevelopTaskVersionDetail);


    /**
     * ollection<DevelopSysParameter>  -> Collection<DevelopSysParameterResultVO>
     * @param developSysParameterCollection
     * @return
     */
    Collection<DevelopSysParameterResultVO> DevelopSysParameterCollectionToDevelopSysParameterResultVOCollection(Collection<DevelopSysParameter> developSysParameterCollection);

    /**
     * List<DevelopResource>  -> List<DevelopResourceResultVO>
     * @param DevelopResourceList
     * @return
     */
    List<DevelopResourceResultVO> DevelopResourceListToDevelopResourceResultVOList(List<DevelopResource> DevelopResourceList);


    /**
     * DevelopTaskDevelopVO -> DevelopTaskGetTaskByIdResultVO
     * @return
     */
    DevelopTaskGetTaskByIdResultVO DevelopTaskDevelopVOToDevelopTaskGetTaskByIdResultVO(DevelopTaskDevelopVO developTaskDevelopVO);

    /**
     * TaskCheckResultVO -> DevelopTaskPublishTaskResultVO
     * @param taskCheckResultVO
     * @return
     */
    DevelopTaskPublishTaskResultVO TaskCheckResultVOToDevelopTaskPublishTaskResultVO(TaskCheckResultVO taskCheckResultVO);


    DevelopUserResultVO dtpToResultVO(UserDTO value);

    /**
     * List<TaskGetNotDeleteVO> -> List<DevelopPreDeleteTaskResultVO>
     * @param notDeleteTaskVOS
     * @return
     */
    List<DevelopGetChildTasksResultVO> notDeleteTaskVOsToDevelopGetChildTasksResultVOs(List<TaskGetNotDeleteVO> notDeleteTaskVOS);

    void taskToTaskVO(Task task, @MappingTarget TaskVO taskVO);

    TaskVO TaskResourceParamToTaskVO(TaskResourceParam taskResourceParam);

    TaskCatalogueResultVO TaskVOToResultVO(TaskVO addOrUpdateTask);

    TaskVO DevelopScheduleTaskVToTaskVO(DevelopScheduleTaskVO DevelopScheduleTaskVO);

    DevelopTaskGetTaskByIdResultVO TaskVOToDevelopTaskGetTaskByIdResultVO(TaskVO taskById);
}
