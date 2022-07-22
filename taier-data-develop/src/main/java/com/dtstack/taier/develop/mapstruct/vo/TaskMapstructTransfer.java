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
import com.dtstack.taier.develop.dto.devlop.TaskCatalogueVO;
import com.dtstack.taier.develop.dto.devlop.TaskCheckResultVO;
import com.dtstack.taier.develop.dto.devlop.TaskGetNotDeleteVO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopScheduleTaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskResourceParamVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopGetChildTasksResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopResourceResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopSysParameterResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskGetTaskByIdResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskPublishTaskResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskResultVO;
import com.dtstack.taier.develop.vo.develop.result.TaskCatalogueResultVO;
import com.dtstack.taier.develop.vo.develop.result.TaskListResultVO;
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
     * TaskCheckResultVO -> DevelopTaskPublishTaskResultVO
     * @param taskCheckResultVO
     * @return
     */
    DevelopTaskPublishTaskResultVO TaskCheckResultVOToDevelopTaskPublishTaskResultVO(TaskCheckResultVO taskCheckResultVO);


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
