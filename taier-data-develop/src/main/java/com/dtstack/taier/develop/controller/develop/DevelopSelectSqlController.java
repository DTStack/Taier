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

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.TaskConfiguration;
import com.dtstack.taier.develop.service.develop.impl.DevelopSelectSqlService;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskService;
import com.dtstack.taier.develop.vo.develop.query.DevelopSelectSqlVO;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "执行选中的sql或者脚本", tags = {"执行选中的sql或者脚本"})
@RestController
@RequestMapping(value = "/batchSelectSql")
public class DevelopSelectSqlController {

    @Autowired
    private TaskConfiguration taskConfiguration;

    @Autowired
    private DevelopSelectSqlService developSelectSqlService;

    @Autowired
    private DevelopTaskService developTaskService;

    @PostMapping(value = "selectData")
    @ApiOperation("获取执行结果")
    public R<ExecuteResultVO> selectData(@RequestBody DevelopSelectSqlVO sqlVO) {
        return new APITemplate<ExecuteResultVO>() {
            @Override
            protected ExecuteResultVO process() {
                try {
                    DevelopSelectSql selectSql = developSelectSqlService.getByJobId(sqlVO.getJobId(), sqlVO.getTenantId(), null);
                    Preconditions.checkNotNull(selectSql, "不存在该临时查询");
                    ITaskRunner taskRunner = taskConfiguration.get(selectSql.getTaskType());
                    Task task = developTaskService.getOneWithError(sqlVO.getTaskId());
                    return taskRunner.selectData(task, selectSql, task.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), selectSql.getTaskType());
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectStatus")
    @ApiOperation("获取执行状态")
    public R<ExecuteResultVO> selectStatus(@RequestBody DevelopSelectSqlVO sqlVO) {
        return new APITemplate<ExecuteResultVO>() {
            @Override
            protected ExecuteResultVO process() {
                try {
                    DevelopSelectSql selectSql = developSelectSqlService.getByJobId(sqlVO.getJobId(), sqlVO.getTenantId(), null);
                    Preconditions.checkNotNull(selectSql, "不存在该临时查询");
                    ITaskRunner taskRunner = taskConfiguration.get(selectSql.getTaskType());
                    Task task = developTaskService.getOneWithError(sqlVO.getTaskId());
                    return taskRunner.selectStatus(task, selectSql, task.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), selectSql.getTaskType());
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectRunLog")
    @ApiOperation("获取执行日志")
    public R<ExecuteResultVO> selectRunLog(@RequestBody DevelopSelectSqlVO sqlVO) {
        return new APITemplate<ExecuteResultVO>() {
            @Override
            protected ExecuteResultVO process() {
                try {
                    DevelopSelectSql selectSql = developSelectSqlService.getByJobId(sqlVO.getJobId(), sqlVO.getTenantId(), null);
                    Preconditions.checkNotNull(selectSql, "不存在该临时查询");
                    ITaskRunner taskRunner = taskConfiguration.get(selectSql.getTaskType());
                    Task task = developTaskService.getOneWithError(sqlVO.getTaskId());
                    return taskRunner.runLog(selectSql.getJobId(), task.getTaskType(), task.getTenantId(), sqlVO.getLimitNum());
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }
}
