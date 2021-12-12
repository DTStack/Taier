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

package com.dtstack.batch.controller.batch;

import com.dtstack.batch.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskResultVO;
import com.dtstack.batch.web.task.vo.query.BatchTaskTaskGetAllFlowSubTasksVO;
import com.dtstack.batch.web.task.vo.query.BatchTaskTaskShadeAddOrUpdateVO;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "提交任务之间关系管理", tags = {"提交任务之间关系管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchTaskTaskShade")
public class BatchTaskTaskShadeController {

    @Autowired
    private ScheduleTaskTaskShadeService schduleTaskTaskShadeRpcService;

    @PostMapping(value = "addOrUpdateTaskTask")
    @ApiOperation("添加或者修改任务之间的关系")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchScheduleTaskResultVO> addOrUpdateTaskTask(@RequestBody BatchTaskTaskShadeAddOrUpdateVO shadeVO) {
        return new APITemplate<BatchScheduleTaskResultVO>() {
            @Override
            protected BatchScheduleTaskResultVO process() {
                ScheduleTaskVO scheduleTaskVO = schduleTaskTaskShadeRpcService.displayOffSpring(shadeVO.getTaskId(),
                        shadeVO.getProjectId(),  shadeVO.getLevel(), shadeVO.getDirectType(), AppType.RDOS.getType());

                return TaskMapstructTransfer.INSTANCE.ScheduleTaskVOToBatchScheduleTaskResultVO(scheduleTaskVO);
            }
        }.execute();
    }

    @PostMapping(value = "getAllFlowSubTasks")
    @ApiOperation("查询工作流全部节点信息")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchScheduleTaskResultVO> getAllFlowSubTasks(@RequestBody BatchTaskTaskGetAllFlowSubTasksVO shadeVO) {
        return new APITemplate<BatchScheduleTaskResultVO>() {
            @Override
            protected BatchScheduleTaskResultVO process() {
                ScheduleTaskVO scheduleTaskVO = schduleTaskTaskShadeRpcService.getAllFlowSubTasks(shadeVO.getTaskId(), AppType.RDOS.getType());
                return TaskMapstructTransfer.INSTANCE.ScheduleTaskVOToBatchScheduleTaskResultVO(scheduleTaskVO);
            }
        }.execute();
    }

}
