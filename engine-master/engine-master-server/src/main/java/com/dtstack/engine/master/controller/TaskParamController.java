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

import com.dtstack.engine.master.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.master.vo.template.TaskTemplateVO;
import com.dtstack.engine.master.impl.TaskParamTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:28 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Api(tags = {"任务参数"})
@RestController
@RequestMapping("/node")
public class TaskParamController {

    @Autowired
    private TaskParamTemplateService taskParamTemplateService;

    @ApiOperation("获取指定任务类型的任务参数 \n 用户替换console的接口:/api/console/service/taskParam/getEngineParamTmplByComputeType")
    @PostMapping("/taskParam/getEngineParamTmplByComputeType")
    public TaskTemplateResultVO getEngineParamTmplByComputeType(@RequestBody TaskTemplateVO param) {
        return taskParamTemplateService.getEngineParamTmplByComputeType(param.getEngineType(), param.getComputeType(), param.getTaskType());
    }

}
