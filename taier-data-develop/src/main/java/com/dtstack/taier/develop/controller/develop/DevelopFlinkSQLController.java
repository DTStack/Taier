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

import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.common.util.JobClientUtil;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.develop.service.develop.impl.FlinkSqlTaskService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.vo.develop.query.StartFlinkSqlVO;
import com.dtstack.taier.develop.vo.develop.result.StartFlinkSqlResultVO;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.service.EngineJobCacheService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@Api(value = "FlinkSQL任务管理", tags = {"FlinkSQL任务管理"})
@RestController
@RequestMapping(value = "/flinkSql")
public class DevelopFlinkSQLController {

    @Autowired
    private FlinkSqlTaskService flinkSqlTaskService;

    @Autowired
    private JobService jobService;

    @Autowired
    private EngineJobCacheService engineJobCacheService;

    @Autowired
    private WorkerOperator workerOperator;


    @ApiOperation(value = "运行FlinkSQL任务")
    @PostMapping(value = "start")
    public R<StartFlinkSqlResultVO> startFlinkSql(@RequestBody StartFlinkSqlVO vo) {
        return R.ok(flinkSqlTaskService.startFlinkSql(vo.getTaskId(), vo.getExternalPath()));
    }


    @ApiOperation(value = "获取实时计算运行中任务的日志URL")
    @PostMapping(value = "getRunningTaskLogUrl")
    public R<List<String>> getRunningTaskLogUrl(@RequestParam("jobId") String jobId) {
        ScheduleJob scheduleJob = jobService.getScheduleJob(jobId);
        return new APITemplate<List<String>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {

                Preconditions.checkState(StringUtils.isNotEmpty(jobId), "jobId can't be empty");
                Preconditions.checkNotNull(scheduleJob, "can't find record by jobId" + jobId);

                //只获取运行中的任务的log—url
                Integer status = scheduleJob.getStatus();
                if (!TaskStatus.RUNNING.getStatus().equals(status)) {
                    throw new RdosDefineException(String.format("job:%s not running status ", jobId), ErrorCode.INVALID_TASK_STATUS);
                }

                String applicationId = scheduleJob.getApplicationId();

                if (StringUtils.isEmpty(applicationId)) {
                    throw new RdosDefineException(String.format("job %s not running in perjob", jobId), ErrorCode.INVALID_TASK_RUN_MODE);
                }
            }

            @Override
            protected List<String> process() throws RdosDefineException {
                try {
                    ScheduleEngineJobCache engineJobCache = engineJobCacheService.getByJobId(jobId);
                    if (engineJobCache == null) {
                        throw new RdosDefineException(String.format("job:%s not exist in job cache table ", jobId), ErrorCode.JOB_CACHE_NOT_EXIST);
                    }
                    String jobInfo = engineJobCache.getJobInfo();
                    ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

                    JobIdentifier jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), jobId, scheduleJob.getTenantId(),
                            scheduleJob.getTaskType(),
                            EDeployMode.PERJOB.getType(), scheduleJob.getCreateUserId(), null, paramAction.getComponentVersion());
                    return workerOperator.getRollingLogBaseInfo(jobIdentifier);
                } catch (Exception e) {
                    throw new RdosDefineException(String.format("get job:%s ref application url error..", jobId), ErrorCode.UNKNOWN_ERROR, e);
                }
            }
        }.execute();
    }


    @PostMapping(value = "/grammarCheck")
    @ApiOperation(value = "语法检测")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramActionExt", value = "语法检测相关参数信息", required = true, paramType = "body", dataType = "ParamActionExt")
    })
    public R<CheckResult> grammarCheck(@RequestBody ParamActionExt paramActionExt) {
        CheckResult checkResult = null;
        try {
            JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
            checkResult = workerOperator.grammarCheck(jobClient);
        } catch (Exception e) {
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return R.ok(checkResult);
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

}
