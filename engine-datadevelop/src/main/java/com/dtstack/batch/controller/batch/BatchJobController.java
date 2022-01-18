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

package com.dtstack.batch.controller.batch;

import com.dtstack.batch.mapstruct.vo.BatchJobMapstructTransfer;
import com.dtstack.batch.service.job.impl.BatchJobService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.web.job.vo.query.BatchJobFindTaskRuleJobVO;
import com.dtstack.batch.web.job.vo.query.BatchJobGetEngineJobIdVO;
import com.dtstack.batch.web.job.vo.query.BatchJobListJobIdByNameVO;
import com.dtstack.batch.web.job.vo.query.BatchJobStartSqlVO;
import com.dtstack.batch.web.job.vo.query.BatchJobStartSyncVO;
import com.dtstack.batch.web.job.vo.query.BatchJobStatisticsTaskRecentInfoVO;
import com.dtstack.batch.web.job.vo.query.BatchJobStopJobVO;
import com.dtstack.batch.web.job.vo.query.BatchJobSyncTaskVO;
import com.dtstack.batch.web.job.vo.query.BatchJobUpdateStatusByIdVO;
import com.dtstack.batch.web.job.vo.query.BatchJobUpdateStatusVO;
import com.dtstack.batch.web.job.vo.result.BatchExecuteResultVO;
import com.dtstack.batch.web.job.vo.result.BatchGetLabTaskRelationMapResultVO;
import com.dtstack.batch.web.job.vo.result.BatchGetSyncTaskStatusInnerResultVO;
import com.dtstack.batch.web.job.vo.result.BatchJobFindTaskRuleJobResultVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleJobExeStaticsResultVO;
import com.dtstack.batch.web.job.vo.result.BatchStartSyncResultVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.lang.coc.APITemplate;
import com.dtstack.engine.common.lang.web.R;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.vo.ScheduleJobExeStaticsVO;
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
    @PostMapping(value = "updateStatusById")
    public R<String> updateStatusById(@RequestBody BatchJobUpdateStatusByIdVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws RdosDefineException {
                return batchJobService.updateStatusById(vo.getJobId(), vo.getStatus());
            }
        }.execute();
    }

    @ApiOperation(value = "更新状态")
    @PostMapping(value = "updateStatus")
    public R<String> updateStatus(@RequestBody BatchJobUpdateStatusVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws RdosDefineException {
                return batchJobService.updateStatus(vo.getJobId(), vo.getStatus(), vo.getMsg());
            }
        }.execute();
    }

    @ApiOperation(value = "停止任务")
    @PostMapping(value = "stopJob")
    public R<String> stopJob(@RequestBody BatchJobStopJobVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws RdosDefineException {
                return batchJobService.stopJob(vo.getJobId(), vo.getUserId(), vo.getIsRoot());
            }
        }.execute();
    }

    @ApiOperation(value = "运行同步任务")
    @PostMapping(value = "startSyncImmediately")
    public R<BatchStartSyncResultVO> startSyncImmediately(@RequestBody BatchJobStartSyncVO vo) {

        return new APITemplate<BatchStartSyncResultVO>() {

            @Override
            protected BatchStartSyncResultVO process() throws RdosDefineException {
                return batchJobService.startSyncImmediately(vo.getTaskId(), vo.getUserId(), vo.getIsRoot(), vo.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取同步任务运行状态")
    @PostMapping(value = "getSyncTaskStatus")
    public R<BatchGetSyncTaskStatusInnerResultVO> getSyncTaskStatus(@RequestBody BatchJobSyncTaskVO vo) {

        return new APITemplate<BatchGetSyncTaskStatusInnerResultVO>() {
            @Override
            protected BatchGetSyncTaskStatusInnerResultVO process() throws RdosDefineException {
                return batchJobService.getSyncTaskStatus(vo.getTenantId(), vo.getJobId(), vo.getUserId());
            }
        }.execute();
    }

    @ApiOperation(value = "停止同步任务")
    @PostMapping(value = "stopSyncJob")
    public R<Void> stopSyncJob(@RequestBody BatchJobSyncTaskVO vo) {

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
    public R<BatchExecuteResultVO> startSqlImmediately(@RequestBody BatchJobStartSqlVO vo) {

        return new APITemplate<BatchExecuteResultVO>() {
            @Override
            protected BatchExecuteResultVO process() throws RdosDefineException {
                ExecuteResultVO executeResultVO = batchJobService.startSqlImmediately(vo.getUserId(), vo.getTenantId(), vo.getTaskId(), vo.getUniqueKey(), vo.getSql(), vo.getTaskVariables(), vo.getDtToken(), vo.getIsCheckDDL(), vo.getIsRoot(), vo.getIsEnd());
                return BatchJobMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteResultVO(executeResultVO);
            }
        }.execute();
    }


    @ApiOperation(value = "停止通过sql任务执行的sql查询语句")
    @PostMapping(value = "stopSqlImmediately")
    public R<Void> stopSqlImmediately(@RequestBody BatchJobSyncTaskVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws RdosDefineException {
                batchJobService.stopSqlImmediately(vo.getJobId(), vo.getTenantId());
                return null;
            }
        }.execute();
    }

    @ApiOperation(value = "运行报告")
    @PostMapping(value = "statisticsTaskRecentInfo")
    public R<BatchScheduleJobExeStaticsResultVO> statisticsTaskRecentInfo(@RequestBody BatchJobStatisticsTaskRecentInfoVO vo) {

        return new APITemplate<BatchScheduleJobExeStaticsResultVO>() {
            @Override
            protected BatchScheduleJobExeStaticsResultVO process() throws RdosDefineException {
                ScheduleJobExeStaticsVO scheduleJobExeStaticsVO = batchJobService.statisticsTaskRecentInfo(vo.getTaskId(), vo.getCount(), vo.getTenantId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobExeStaticsVOToBatchScheduleJobExeStaticsResultVO(scheduleJobExeStaticsVO);
            }
        }.execute();
    }


    @ApiOperation(value = "根据任务名称和状态列表得到实例Id")
    @PostMapping(value = "listJobIdByTaskNameAndStatusList")
    public R<List<String>> listJobIdByTaskNameAndStatusList(@RequestBody BatchJobListJobIdByNameVO vo) {

        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() throws RdosDefineException {
                return batchJobService.listJobIdByTaskNameAndStatusList(vo.getTaskName(), vo.getStatusList(), vo.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "返回这些jobId对应的父节点的jobMap")
    @PostMapping(value = "getLabTaskRelationMap")
    public R<Map<String, BatchGetLabTaskRelationMapResultVO>> getLabTaskRelationMap(@RequestBody BatchJobListJobIdByNameVO vo) {
        return new APITemplate<Map<String, BatchGetLabTaskRelationMapResultVO>>() {
            @Override
            protected Map<String, BatchGetLabTaskRelationMapResultVO> process() throws RdosDefineException {
                Map<String, ScheduleJob> labTaskRelationMap = batchJobService.getLabTaskRelationMap(vo.getJobIdList(), vo.getTenantId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobMapToBatchGetLabTaskRelationMapResultVOMap(labTaskRelationMap);
            }
        }.execute();
    }

    @ApiOperation(value = "根据实例Id获取引擎")
    @PostMapping(value = "getEngineJobId")
    public R<String> getEngineJobId(@RequestBody BatchJobGetEngineJobIdVO vo) {
        return new APITemplate<String>() {
            @Override
            protected String process() throws RdosDefineException {
                return batchJobService.getEngineJobId(vo.getJobId());
            }
        }.execute();
    }

    @ApiOperation(value = "根据实例Id获取任务信息，hover事件详情信息")
    @PostMapping(value = "findTaskRuleJob")
    public R<BatchJobFindTaskRuleJobResultVO> findTaskRuleJob(@RequestBody BatchJobFindTaskRuleJobVO vo) {
        return new APITemplate<BatchJobFindTaskRuleJobResultVO>() {
            @Override
            protected BatchJobFindTaskRuleJobResultVO process() throws RdosDefineException {
                return null;
//                return BatchJobMapstructTransfer.INSTANCE.scheduleDetailsVOToBatchJobFindTaskRuleJobResultVO(batchJobService.findTaskRuleJob(vo.getJobId()));
            }
        }.execute();
    }

}
