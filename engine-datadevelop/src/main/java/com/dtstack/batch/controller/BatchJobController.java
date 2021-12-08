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

import com.dtstack.batch.mapstruct.vo.BatchJobMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.job.impl.BatchJobService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.web.job.vo.query.*;
import com.dtstack.batch.web.job.vo.result.*;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.vo.*;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(value = "任务实例管理", tags = {"任务实例管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchJob")
public class BatchJobController {

    @Autowired
    private BatchJobService batchJobService;

    @ApiOperation(value = "通过ID更新状态")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "updateStatusById")
    public R<String> updateStatusById(@RequestBody BatchJobUpdateStatusByIdVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return batchJobService.updateStatusById(vo.getJobId(), vo.getStatus());
            }
        }.execute();
    }

    @ApiOperation(value = "更新状态")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "updateStatus")
    public R<String> updateStatus(@RequestBody BatchJobUpdateStatusVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return batchJobService.updateStatus(vo.getJobId(), vo.getStatus(), vo.getMsg());
            }
        }.execute();
    }

    @ApiOperation(value = "停止任务")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "stopJob")
    public R<String> stopJob(@RequestBody BatchJobStopJobVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return batchJobService.stopJob(vo.getJobId(), vo.getUserId(), vo.getProjectId(), vo.getTenantId(), vo.getDtuicTenantId(), vo.getIsRoot());
            }
        }.execute();
    }

    @ApiOperation(value = "运行同步任务")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    @PostMapping(value = "startSyncImmediately")
    public R<BatchStartSyncResultVO> startSyncImmediately(@RequestBody BatchJobStartSyncVO vo) {

        return new APITemplate<BatchStartSyncResultVO>() {

            @Override
            protected BatchStartSyncResultVO process() throws BizException {
                return batchJobService.startSyncImmediately(vo.getTaskId(), vo.getUserId(), vo.getIsRoot(), vo.getTenantId(), vo.getTaskParams());
            }
        }.execute();
    }

    @ApiOperation(value = "获取同步任务运行状态")
    @PostMapping(value = "getSyncTaskStatus")
    public R<BatchGetSyncTaskStatusInnerResultVO> getSyncTaskStatus(@RequestBody BatchJobSyncTaskVO vo) {

        return new APITemplate<BatchGetSyncTaskStatusInnerResultVO>() {
            @Override
            protected BatchGetSyncTaskStatusInnerResultVO process() throws BizException {
                return batchJobService.getSyncTaskStatus(vo.getTenantId(), vo.getJobId(), vo.getUserId(), vo.getProjectId());
            }
        }.execute();
    }

    @ApiOperation(value = "停止同步任务")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    @PostMapping(value = "stopSyncJob")
    public R<Void> stopSyncJob(@RequestBody BatchJobSyncTaskVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                batchJobService.stopSyncJob(vo.getJobId());
                return null;
            }
        }.execute();
    }

    @ApiOperation(value = "运行sql")
    @PostMapping(value = "startSqlImmediately")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    public R<BatchExecuteResultVO> startSqlImmediately(@RequestBody BatchJobStartSqlVO vo) {

        return new APITemplate<BatchExecuteResultVO>() {
            @Override
            protected BatchExecuteResultVO process() throws BizException {
                ExecuteResultVO executeResultVO = batchJobService.startSqlImmediately(vo.getUserId(), vo.getTenantId(), vo.getProjectId(), vo.getTaskId(), vo.getUniqueKey(), vo.getSql(), vo.getTaskVariables(), vo.getDtToken(), vo.getIsCheckDDL(), vo.getIsRoot(), vo.getIsEnd(), vo.getDtuicTenantId(), vo.getTaskParams());
                return BatchJobMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteResultVO(executeResultVO);
            }
        }.execute();
    }

    @ApiOperation(value = "高级运行sparkSql从引擎执行逻辑")
    @PostMapping(value = "startSqlSophisticated")
    public R<BatchExecuteSqlParseResultVO> startSqlSophisticated(@RequestBody BatchJobStartSqlSophisticatedVO vo) {

        return new APITemplate<BatchExecuteSqlParseResultVO>() {
            @Override
            protected BatchExecuteSqlParseResultVO process() throws BizException {
                ExecuteSqlParseVO executeSqlParseVO = batchJobService.startSqlSophisticated(vo.getUserId(), vo.getTenantId(), vo.getProjectId(), vo.getTaskId(), vo.getUniqueKey(), vo.getSqlList(), vo.getTaskVariables(), vo.getDtToken(), vo.getIsCheckDDL(), vo.getIsRoot(), vo.getDtuicTenantId());
                return BatchJobMapstructTransfer.INSTANCE.executeSqlParseVOToBatchExecuteSqlParseResultVO(executeSqlParseVO);
            }
        }.execute();
    }


    @ApiOperation(value = "停止通过sql任务执行的sql查询语句")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    @PostMapping(value = "stopSqlImmediately")
    public R<Void> stopSqlImmediately(@RequestBody BatchJobSyncTaskVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                batchJobService.stopSqlImmediately(vo.getJobId(), vo.getTenantId(), vo.getProjectId(), vo.getDtuicTenantId());
                return null;
            }
        }.execute();
    }

    @ApiOperation(value = "运行报告")
    @PostMapping(value = "statisticsTaskRecentInfo")
    public R<BatchScheduleJobExeStaticsResultVO> statisticsTaskRecentInfo(@RequestBody BatchJobStatisticsTaskRecentInfoVO vo) {

        return new APITemplate<BatchScheduleJobExeStaticsResultVO>() {
            @Override
            protected BatchScheduleJobExeStaticsResultVO process() throws BizException {
                ScheduleJobExeStaticsVO scheduleJobExeStaticsVO = batchJobService.statisticsTaskRecentInfo(vo.getTaskId(), vo.getCount(), vo.getProjectId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobExeStaticsVOToBatchScheduleJobExeStaticsResultVO(scheduleJobExeStaticsVO);
            }
        }.execute();
    }


    @ApiOperation(value = "根据任务名称和状态列表得到实例Id")
    @PostMapping(value = "listJobIdByTaskNameAndStatusList")
    public R<List<String>> listJobIdByTaskNameAndStatusList(@RequestBody BatchJobListJobIdByNameVO vo) {

        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() throws BizException {
                return batchJobService.listJobIdByTaskNameAndStatusList(vo.getTaskName(), vo.getStatusList(), vo.getProjectId());
            }
        }.execute();
    }

    @ApiOperation(value = "返回这些jobId对应的父节点的jobMap")
    @PostMapping(value = "getLabTaskRelationMap")
    public R<Map<String, BatchGetLabTaskRelationMapResultVO>> getLabTaskRelationMap(@RequestBody BatchJobListJobIdByNameVO vo) {
        return new APITemplate<Map<String, BatchGetLabTaskRelationMapResultVO>>() {
            @Override
            protected Map<String, BatchGetLabTaskRelationMapResultVO> process() throws BizException {
                Map<String, ScheduleJob> labTaskRelationMap = batchJobService.getLabTaskRelationMap(vo.getJobIdList(), vo.getProjectId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobMapToBatchGetLabTaskRelationMapResultVOMap(labTaskRelationMap);
            }
        }.execute();
    }

    @ApiOperation(value = "根据实例Id获取引擎")
    @PostMapping(value = "getEngineJobId")
    public R<String> getEngineJobId(@RequestBody BatchJobGetEngineJobIdVO vo) {
        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return batchJobService.getEngineJobId(vo.getJobId());
            }
        }.execute();
    }

    @ApiOperation(value = "根据实例Id获取任务信息，hover事件详情信息")
    @PostMapping(value = "findTaskRuleJob")
    public R<BatchJobFindTaskRuleJobResultVO> findTaskRuleJob(@RequestBody BatchJobFindTaskRuleJobVO vo) {
        return new APITemplate<BatchJobFindTaskRuleJobResultVO>() {
            @Override
            protected BatchJobFindTaskRuleJobResultVO process() throws BizException {
                return null;
//                return BatchJobMapstructTransfer.INSTANCE.scheduleDetailsVOToBatchJobFindTaskRuleJobResultVO(batchJobService.findTaskRuleJob(vo.getJobId()));
            }
        }.execute();
    }

}
