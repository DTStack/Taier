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

package com.dtstack.batch.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.mapstruct.vo.BatchServerLogMapstructTransfer;
import com.dtstack.batch.service.impl.BatchServerLogService;
import com.dtstack.batch.vo.BatchServerLogVO;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByAppIdVO;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByAppTypeVO;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByJobIdVO;
import com.dtstack.batch.web.server.vo.result.BatchServerLogByAppLogTypeResultVO;
import com.dtstack.batch.web.server.vo.result.BatchServerLogResultVO;
import com.dtstack.engine.common.lang.coc.APITemplate;
import com.dtstack.engine.common.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "日志管理", tags = {"日志管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchServerLog")
public class BatchServerLogController {

    @Autowired
    private BatchServerLogService batchServerLogService;

    @PostMapping(value = "getLogsByJobId")
    @ApiOperation("根据jobId获取日志")
    public R<BatchServerLogResultVO> getLogsByJobId(@RequestBody BatchServerGetLogByJobIdVO vo) {
        return new APITemplate<BatchServerLogResultVO>() {
            @Override
            protected BatchServerLogResultVO process() {
                BatchServerLogVO logsByJobId = batchServerLogService.getLogsByJobId(vo.getJobId(), vo.getPageInfo());
                return BatchServerLogMapstructTransfer.INSTANCE.batchServerLogVOToBatchServerLogResultVO(logsByJobId);
            }
        }.execute();
    }

    @PostMapping(value = "getLogsByAppId")
    @ApiOperation("根据appId获取日志")
    public R<JSONObject> getLogsByAppId(@RequestBody BatchServerGetLogByAppIdVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchServerLogService.getLogsByAppId(vo.getDtuicTenantId(), vo.getTaskType(), vo.getJobId(), vo.getProjectId());
            }
        }.execute();
    }

    @PostMapping(value = "getLogsByAppLogType")
    @ApiOperation("根据类型获取日志")
    public R<BatchServerLogByAppLogTypeResultVO> getLogsByAppLogType(@RequestBody BatchServerGetLogByAppTypeVO vo) {
        return new APITemplate<BatchServerLogByAppLogTypeResultVO>() {
            @Override
            protected BatchServerLogByAppLogTypeResultVO process() {
                return batchServerLogService.getLogsByAppLogType(vo.getDtuicTenantId(), vo.getTaskType(), vo.getJobId(), vo.getLogType(), vo.getProjectId());
            }
        }.execute();
    }
}
