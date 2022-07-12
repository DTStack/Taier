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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.mapstruct.vo.DevelopServerLogMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopServerLogService;
import com.dtstack.taier.develop.dto.devlop.DevelopServerLogVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopServerGetLogByAppIdVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopServerGetLogByAppTypeVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopServerGetLogByJobIdVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopServerLogByAppLogTypeResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopServerLogResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "日志管理", tags = {"日志管理"})
@RestController
@RequestMapping(value = "/batchServerLog")
public class DevelopServerLogController {

    @Autowired
    private DevelopServerLogService batchServerLogService;

    @PostMapping(value = "getLogsByJobId")
    @ApiOperation("根据jobId获取日志")
    public R<DevelopServerLogResultVO> getLogsByJobId(@RequestBody DevelopServerGetLogByJobIdVO vo) {
        return new APITemplate<DevelopServerLogResultVO>() {
            @Override
            protected DevelopServerLogResultVO process() {
                DevelopServerLogVO logsByJobId = batchServerLogService.getLogsByJobId(vo.getJobId(), vo.getPageInfo());
                return DevelopServerLogMapstructTransfer.INSTANCE.batchServerLogVOToDevelopServerLogResultVO(logsByJobId);
            }
        }.execute();
    }

    @PostMapping(value = "getLogsByAppId")
    @ApiOperation("根据appId获取日志")
    public R<JSONObject> getLogsByAppId(@RequestBody DevelopServerGetLogByAppIdVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchServerLogService.getLogsByAppId(vo.getTenantId(), vo.getTaskType(), vo.getJobId());
            }
        }.execute();
    }

    @PostMapping(value = "getLogsByAppLogType")
    @ApiOperation("根据类型获取日志")
    public R<DevelopServerLogByAppLogTypeResultVO> getLogsByAppLogType(@RequestBody DevelopServerGetLogByAppTypeVO vo) {
        return new APITemplate<DevelopServerLogByAppLogTypeResultVO>() {
            @Override
            protected DevelopServerLogByAppLogTypeResultVO process() {
                return batchServerLogService.getLogsByAppLogType(vo.getTenantId(), vo.getTaskType(), vo.getJobId(), vo.getLogType());
            }
        }.execute();
    }
}
