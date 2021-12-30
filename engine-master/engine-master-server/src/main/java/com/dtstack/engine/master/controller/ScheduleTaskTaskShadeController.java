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

package com.dtstack.engine.master.controller;

import com.dtstack.engine.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.vo.task.SaveTaskTaskVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/scheduleTaskTaskShade")
@Api(value = "/node/scheduleTaskTaskShade", tags = {"任务依赖接口"})
public class ScheduleTaskTaskShadeController {

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @RequestMapping(value="/clearDataByTaskId", method = {RequestMethod.POST})
    public void clearDataByTaskId(@RequestParam("taskId") Long taskId, @RequestParam("appType")Integer appType) {
        scheduleTaskTaskShadeService.clearDataByTaskId(taskId, appType);
    }

    @RequestMapping(value="/saveTaskTaskList", method = {RequestMethod.POST})
    public SaveTaskTaskVO saveTaskTaskList(@RequestParam("taskTask") String taskLists, @RequestParam("commitId") String commitId) {
        return scheduleTaskTaskShadeService.saveTaskTaskList(taskLists);
    }

    @RequestMapping(value="/getAllParentTask", method = {RequestMethod.POST})
    public List<ScheduleTaskTaskShade> getAllParentTask(@RequestParam("taskId") Long taskId, @RequestParam("appType")Integer appType) {
        return scheduleTaskTaskShadeService.getAllParentTask(taskId,appType);
    }

//    @RequestMapping(value="/displayOffSpring", method = {RequestMethod.POST})
//    public ScheduleTaskVO displayOffSpring(@RequestParam("taskId") Long taskId,
//                                           @RequestParam("projectId") Long projectId,
//                                           @RequestParam("level") Integer level,
//                                           @RequestParam("type") Integer directType, @RequestParam("appType")Integer appType) {
//        return scheduleTaskTaskShadeService.displayOffSpring(taskId, projectId, level, directType, appType);
//    }

//    @RequestMapping(value="/getAllFlowSubTasks", method = {RequestMethod.POST})
//    @ApiOperation(value = "查询工作流全部节点信息 -- 依赖树")
//    public ScheduleTaskVO getAllFlowSubTasks(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType) {
//        return scheduleTaskTaskShadeService.getAllFlowSubTasks(taskId, appType);
//    }
}
