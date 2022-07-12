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
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.mapstruct.vo.DevelopJobMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopJobService;
import com.dtstack.taier.develop.vo.develop.query.DevelopJobStartSqlVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopJobStartSyncVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopJobSyncTaskVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopGetSyncTaskStatusInnerResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopStartSyncResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "任务实例管理", tags = {"任务实例管理"})
@RestController
@RequestMapping(value = "/batchJob")
public class DevelopJobController {

    @Autowired
    private DevelopJobService batchJobService;


    @ApiOperation(value = "运行同步任务")
    @PostMapping(value = "startSyncImmediately")
    public R<DevelopStartSyncResultVO> startSyncImmediately(@RequestBody DevelopJobStartSyncVO vo) {

        return new APITemplate<DevelopStartSyncResultVO>() {

            @Override
            protected DevelopStartSyncResultVO process() throws RdosDefineException {
                return batchJobService.startSyncImmediately(vo.getTaskId(), vo.getUserId(), vo.getIsRoot(), vo.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取同步任务运行状态")
    @PostMapping(value = "getSyncTaskStatus")
    public R<DevelopGetSyncTaskStatusInnerResultVO> getSyncTaskStatus(@RequestBody DevelopJobSyncTaskVO vo) {

        return new APITemplate<DevelopGetSyncTaskStatusInnerResultVO>() {
            @Override
            protected DevelopGetSyncTaskStatusInnerResultVO process() throws RdosDefineException {
                return batchJobService.getSyncTaskStatus(vo.getTenantId(), vo.getJobId());
            }
        }.execute();
    }

    @ApiOperation(value = "停止同步任务")
    @PostMapping(value = "stopSyncJob")
    public R<Void> stopSyncJob(@RequestBody DevelopJobSyncTaskVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws RdosDefineException {
                batchJobService.stopSyncJob(vo.getJobId());
                return null;
            }
        }.execute();
    }

    @ApiOperation(value = "运行sql")
    @PostMapping(value = "startSqlImmediately")
    public R<DevelopExecuteResultVO> startSqlImmediately(@RequestBody DevelopJobStartSqlVO vo) {

        return new APITemplate<DevelopExecuteResultVO>() {
            @Override
            protected DevelopExecuteResultVO process() throws RdosDefineException {
                ExecuteResultVO executeResultVO = batchJobService.startSqlImmediately(vo.getUserId(), vo.getTenantId(), vo.getTaskId(), vo.getSql(), vo.getTaskVariables());
                return DevelopJobMapstructTransfer.INSTANCE.executeResultVOToDevelopExecuteResultVO(executeResultVO);
            }
        }.execute();
    }


    @ApiOperation(value = "停止通过sql任务执行的sql查询语句")
    @PostMapping(value = "stopSqlImmediately")
    public R<Void> stopSqlImmediately(@RequestBody DevelopJobSyncTaskVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws RdosDefineException {
                batchJobService.stopSqlImmediately(vo.getJobId(), vo.getTenantId());
                return null;
            }
        }.execute();
    }


}
