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

package com.dtstack.taier.develop.controller.develop;

import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.dto.devlop.FlinkSqlTaskManagerVO;
import com.dtstack.taier.develop.dto.devlop.RuntimeLogResultVO;
import com.dtstack.taier.develop.service.develop.impl.FlinkSqlRuntimeLogService;
import com.dtstack.taier.develop.service.develop.impl.FlinkSqlTaskService;
import com.dtstack.taier.develop.vo.develop.query.RuntimeLogQueryVO;
import com.dtstack.taier.develop.vo.develop.query.StartFlinkSqlVO;
import com.dtstack.taier.develop.vo.develop.query.TaskIdQueryVO;
import com.dtstack.taier.develop.vo.develop.query.TaskSearchVO;
import com.dtstack.taier.develop.vo.develop.result.StartFlinkSqlResultVO;
import com.dtstack.taier.develop.vo.develop.result.TaskListResultVO;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "FlinkSQL任务管理", tags = {"FlinkSQL任务管理"})
@RestController
@RequestMapping(value = "/flinkSql")
public class DevelopFlinkSQLController {

    @Autowired
    private FlinkSqlTaskService flinkSqlTaskService;

    @Autowired
    private FlinkSqlRuntimeLogService flinkSqlRuntimeLogService;


    @ApiOperation(value = "运行FlinkSQL任务")
    @PostMapping(value = "start")
    public R<StartFlinkSqlResultVO> startFlinkSql(@RequestBody StartFlinkSqlVO vo) {
        return R.ok(flinkSqlTaskService.startFlinkSql(vo.getTaskId(), vo.getExternalPath()));
    }


    @PostMapping(value = "/grammarCheck")
    @ApiOperation(value = "语法检测")
    public R<CheckResult> grammarCheck(@RequestBody StartFlinkSqlVO vo) {
        return new APITemplate<CheckResult>() {
            @Override
            protected CheckResult process() {
                return flinkSqlTaskService.grammarCheck(vo.getTaskId());
            }
        }.execute();
    }

    @ApiOperation("获取所有时区信息")
    @PostMapping(value = "getAllTimeZone")
    public R<List<String>> getAllTimeZone() {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return flinkSqlTaskService.getAllTimeZone();
            }
        }.execute();
    }

    @ApiOperation("根据条件查询任务列表")
    @PostMapping(value = "getTaskList")
    public R<PageResult<List<TaskListResultVO>>> getTaskList(@RequestBody TaskSearchVO taskSearchVO) {
        return new APITemplate<PageResult<List<TaskListResultVO>>>() {
            @Override
            protected PageResult<List<TaskListResultVO>> process() {
                return flinkSqlTaskService.getTaskList(taskSearchVO);
            }
        }.execute();
    }

    @ApiOperation("获取 TaskManager 信息")
    @PostMapping(value = "listTaskManagerByTaskId")
    public R<List<FlinkSqlTaskManagerVO>> listTaskManagerByTaskId(@RequestBody TaskIdQueryVO taskIdQueryVO) {
        return new APITemplate<List<FlinkSqlTaskManagerVO>>() {
            @Override
            protected List<FlinkSqlTaskManagerVO> process() {
                return flinkSqlRuntimeLogService.listTaskManagerByJobId(taskIdQueryVO.getTaskId(), taskIdQueryVO.getTenantId());
            }
        }.execute();
    }

    @ApiOperation("获取 Master 节点日志信息")
    @PostMapping(value = "getJobManagerLog")
    public R<RuntimeLogResultVO> getJobManagerLog(@RequestBody RuntimeLogQueryVO logQueryVO) {
        return new APITemplate<RuntimeLogResultVO>() {
            @Override
            protected RuntimeLogResultVO process() {
                return flinkSqlRuntimeLogService.getJobManagerLog(logQueryVO.getJobId(), logQueryVO.getPlace(), logQueryVO.getTenantId());
            }
        }.execute();
    }

    @ApiOperation("获取 Worker 节点日志信息")
    @PostMapping(value = "getTaskManagerLog")
    public R<RuntimeLogResultVO> getTaskManagerLog(@RequestBody RuntimeLogQueryVO logQueryVO) {
        return new APITemplate<RuntimeLogResultVO>() {
            @Override
            protected RuntimeLogResultVO process() {
                return flinkSqlRuntimeLogService.getTaskManagerLog(logQueryVO.getJobId(), logQueryVO.getTaskManagerId(), logQueryVO.getCurrentPage(),
                        logQueryVO.getPlace(), logQueryVO.getTenantId());
            }
        }.execute();
    }

}
