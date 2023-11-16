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

package com.dtstack.taier.develop.controller.operation;

import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.TaskService;
import com.dtstack.taier.develop.vo.schedule.QueryTaskListVO;
import com.dtstack.taier.develop.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.taier.develop.vo.schedule.ReturnTaskSupportTypesVO;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 1:58 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/scheduleTaskShade")
@Api(value = "/scheduleTaskShade", tags = {"运维中心---任务相关接口"})
public class OperationScheduleTaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/queryTasks")
    @ApiOperation(value = "运维中心任务管理 -> 任务列表接口")
    public R<PageResult<List<ReturnScheduleTaskVO>>> queryTasks(@RequestBody @Validated QueryTaskListVO vo) {
        return R.ok(taskService.queryTasks(ScheduleTaskMapstructTransfer.INSTANCE.queryTasksVoToDto(vo)));
    }

    @RequestMapping(value = "/queryFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long"),
    })
    public R<List<ReturnScheduleTaskVO>> queryFlowWorkSubTasks(@RequestParam("taskId") Long taskId) {
        return R.ok(ScheduleTaskMapstructTransfer.INSTANCE.beanToTaskVO(taskService.findAllFlowTasks(Lists.newArrayList(taskId))));
    }

    @PostMapping(value = "/querySupportJobTypes")
    @ApiOperation(value = "查询所有任务类型")
    public R<List<ReturnTaskSupportTypesVO>> querySupportJobTypes() {
        return R.ok(taskService.querySupportJobTypes());
    }
}
