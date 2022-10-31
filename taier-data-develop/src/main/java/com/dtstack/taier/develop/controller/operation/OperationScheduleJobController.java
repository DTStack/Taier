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
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.mapstruct.job.JobMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.JobHistoryService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.vo.schedule.JobHistoryVO;
import com.dtstack.taier.develop.vo.schedule.QueryJobListVO;
import com.dtstack.taier.develop.vo.schedule.QueryJobStatusStatisticsVO;
import com.dtstack.taier.develop.vo.schedule.ReturnDisplayPeriodVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobListVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobStatusStatisticsVO;
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
 * @Date: 2021/12/23 3:54 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/scheduleJob")
@Api(value = "/scheduleJob", tags = {"运维中心---周期实例相关接口"})
public class OperationScheduleJobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobHistoryService jobHistoryService;

    @RequestMapping(value = "/queryJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "任务运维 - 搜索")
    public R<PageResult<List<ReturnJobListVO>>> queryJobs(@RequestBody QueryJobListVO vo) {
        return R.ok(jobService.queryJobs(JobMapstructTransfer.INSTANCE.queryJobListVOToQueryJobListDTO(vo)));
    }

    @RequestMapping(value = "/queryJobsStatusStatistics", method = {RequestMethod.POST})
    @ApiOperation(value = "任务状态统计")
    public R<List<ReturnJobStatusStatisticsVO>> queryJobsStatusStatistics(@RequestBody QueryJobStatusStatisticsVO vo) {
        return R.ok(jobService.queryJobsStatusStatistics(JobMapstructTransfer.INSTANCE.queryJobStatusStatisticsVOToQueryJobStatusStatisticsDTO(vo)));
    }

    @RequestMapping(value = "/queryFlowWorkSubJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "获取工作流节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "实例id", required = true, dataType = "String"),
    })
    public R<List<ReturnJobListVO>> queryFlowWorkSubJobs(@RequestParam("jobId") String jobId) {
        return R.ok(jobService.queryFlowWorkSubJobs(jobId));
    }

    @RequestMapping(value = "/queryDisplayPeriods", method = {RequestMethod.POST})
    public R<List<ReturnDisplayPeriodVO>> queryDisplayPeriods(@RequestParam("isAfter") Boolean isAfter,
                                                              @RequestParam("jobId") String jobId,
                                                              @RequestParam("limit") Integer limit) {
        return R.ok(jobService.displayPeriods(isAfter, jobId, limit));
    }

    @PostMapping(value = "/listHistory")
    public R<List<JobHistoryVO>> listHistory(@RequestParam("jobId") String jobId,
                                             @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<ScheduleJobHistory> scheduleJobHistories = jobHistoryService.listHistory(jobId, limit);
        List<JobHistoryVO> jobHistoryVOS = JobMapstructTransfer.INSTANCE.toHistoryVOS(scheduleJobHistories);
        return R.ok(jobHistoryVOS);
    }

    @PostMapping(value = "/getRelatedJobs")
    public R<List<ReturnJobListVO>> getRelatedJobs(@RequestParam("jobId") String jobId) {
        return R.ok(jobService.getRelatedJobs(jobId));
    }

}
