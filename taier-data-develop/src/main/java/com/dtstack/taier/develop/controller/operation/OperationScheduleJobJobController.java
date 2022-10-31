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
import com.dtstack.taier.develop.mapstruct.job.JobMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.JobJobService;
import com.dtstack.taier.develop.vo.schedule.QueryJobDisplayVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobDisplayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 10:26 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/scheduleJobJob")
@Api(value = "/scheduleJobJob", tags = {"运维中心---周期实例依赖关系相关接口"})
public class OperationScheduleJobJobController {

    @Autowired
    private JobJobService jobJobService;

    @RequestMapping(value = "/displayOffSpring", method = {RequestMethod.POST})
    public R<ReturnJobDisplayVO> displayOffSpring(@RequestBody QueryJobDisplayVO vo) {
        return R.ok(jobJobService.displayOffSpring(JobMapstructTransfer.INSTANCE.queryJobDisplayVOToReturnJobDisplayVO(vo)));
    }

    @PostMapping(value = "/getWorkFlowTopJob")
    @ApiOperation(value = "查询工作流顶节点实例信息 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "实例id", required = true, dataType = "String"),
    })
    public R<List<String>> getWorkFlowTopJob(@RequestParam("jobId") String jobId) {
        return R.ok(jobJobService.getWorkFlowTopTask(jobId));
    }
}
