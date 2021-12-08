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

import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.impl.pojo.ParamTaskAction;
import com.dtstack.engine.master.vo.JobLogVO;
import com.dtstack.engine.master.vo.action.ActionJobStatusVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/node/action")
@Api(value = "/node/action", tags = {"任务动作接口"})
public class OldActionController  {

    @Autowired
    private ActionService actionService;

    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    @ApiOperation(value = "开始任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramActionExt", value = "请求开始的任务的相关信息及集群信息", required = true, paramType = "body", dataType = "ParamActionExt")
    })
    public Boolean start(@RequestBody ParamActionExt paramActionExt) {
        return actionService.start(paramActionExt);
    }

    @RequestMapping(value = "/paramActionExt", method = {RequestMethod.POST})
    @ApiOperation(value = "提交前预处理接口")
    public ParamActionExt paramActionExt(@RequestBody ParamTaskAction paramTaskAction) throws Exception {
        return actionService.paramActionExt(paramTaskAction.getBatchTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
    }


}
