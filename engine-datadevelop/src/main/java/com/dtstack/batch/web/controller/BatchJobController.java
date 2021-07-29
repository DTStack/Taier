package com.dtstack.batch.web.controller;

import com.dtstack.batch.mapstruct.vo.BatchJobMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.job.impl.BatchJobService;
import com.dtstack.batch.vo.BatchOperatorVO;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.web.job.vo.query.*;
import com.dtstack.batch.web.job.vo.result.*;
import com.dtstack.batch.web.model.vo.result.BatchChartDataResultVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.*;
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


    @ApiOperation(value = "根据任务id展示任务详情")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "getJobById")
    public R<BatchScheduleGetByJobIdResultVO> getJobById(@RequestBody BatchJobGetJobByIdVO vo) {
        return new APITemplate<BatchScheduleGetByJobIdResultVO>() {
            @Override
            protected BatchScheduleGetByJobIdResultVO process() throws BizException {
                ScheduleJob jobById = batchJobService.getJobById(vo.getJobId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobToBatchScheduleGetByJobIdResultVO(jobById);
            }
        }.execute();
    }

    @ApiOperation(value = "获取任务状态")
    @PostMapping(value = "getJobStatus")
    public R<Integer> getJobStatus(@RequestBody BatchJobGetJobStatusVO vo) {

        return new APITemplate<Integer>() {
            @Override
            protected Integer process() throws BizException {
                return batchJobService.getJobStatus(vo.getJobId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取各个状态任务的数量")
    @Security(code = AuthCode.MAINTENANCE_PANDECT_BATCH)
    @PostMapping(value = "getStatusCount")
    public R<Map<String, Object>> getStatusCount(@RequestBody(required = false) BatchJobBaseVO vo) {

        return new APITemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() throws BizException {
                return batchJobService.getStatusCount(vo.getProjectId(), vo.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "运行时长top排序")
    @Security(code = AuthCode.MAINTENANCE_PANDECT_BATCH)
    @PostMapping(value = "runTimeTopOrder")
    public R<List<BatchJobTopOrderResultVO>> runTimeTopOrder(@RequestBody(required = false) BatchJobRunTimeTopVO vo) {

        return new APITemplate<List<BatchJobTopOrderResultVO>>() {
            @Override
            protected List<BatchJobTopOrderResultVO> process() throws BizException {
                List<JobTopOrderVO> list = batchJobService.runTimeTopOrder(vo.getProjectId(), vo.getStartTime(), vo.getEndTime(), vo.getDtuicTenantId());
                return BatchJobMapstructTransfer.INSTANCE.jobTopOrderVOSToBatchJobTopOrderResultVO(list);
            }
        }.execute();
    }

    @ApiOperation(value = "近30天任务出错排行")
    @Security(code = AuthCode.MAINTENANCE_PANDECT_BATCH)
    @PostMapping(value = "errorTopOrder")
    public R<List<BatchJobTopErrorResultVO>> errorTopOrder(@RequestBody(required = false) BatchJobBaseVO vo) {

        return new APITemplate<List<BatchJobTopErrorResultVO>>() {
            @Override
            protected List<BatchJobTopErrorResultVO> process() throws BizException {
                List<JobTopErrorVO> jobTopErrorVOS = batchJobService.errorTopOrder(vo.getProjectId(), vo.getTenantId());
                return BatchJobMapstructTransfer.INSTANCE.jobTopErrorVOSToBatchJobTopErrorResultVOs(jobTopErrorVOS);
            }
        }.execute();
    }


    @ApiOperation(value = "曲线图数据")
    @Security(code = AuthCode.MAINTENANCE_PANDECT_BATCH)
    @PostMapping(value = "getJobGraph")
    public R<BatchChartDataResultVO> getJobGraph(@RequestBody(required = false) BatchJobBaseVO vo) {

        return new APITemplate<BatchChartDataResultVO>() {
            @Override
            protected BatchChartDataResultVO process() throws BizException {
                ScheduleJobChartVO jobGraph = batchJobService.getJobGraph(vo.getProjectId(), vo.getTenantId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobChartVOToBatchChartDataResultVO(jobGraph);
            }
        }.execute();
    }

    @ApiOperation(value = "任务运维 - 周期实例")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "queryJobs")
    public R<PageResult<List<BatchScheduleJobResultVO>>> queryJobs(@RequestBody BatchJobQueryJobVO vo) {

        return new APITemplate<PageResult<List<BatchScheduleJobResultVO>>>() {
            @Override
            protected PageResult<List<BatchScheduleJobResultVO>> process() throws BizException {
                PageResult<List<ScheduleJobVO>> listPageResult = batchJobService.queryJobs(BatchJobMapstructTransfer.INSTANCE.queryJobVOToQueryJobDTO(vo), vo.getSearchType());
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobVOToBatchScheduleJobResultVO(listPageResult);
            }
        }.execute();
    }

    @ApiOperation(value = "显示周期")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "displayPeriods")
    public R<List<BatchSchedulePeriodInfoResultVO>> displayPeriods(@RequestBody BatchJobDisplayPeriodsVO vo) {

        return new APITemplate<List<BatchSchedulePeriodInfoResultVO>>() {
            @Override
            protected List<BatchSchedulePeriodInfoResultVO> process() throws BizException {
                List<SchedulePeriodInfoVO> schedulePeriodInfoVOS = batchJobService.displayPeriods(vo.getIsAfter(), vo.getJobId(), vo.getProjectId(), vo.getLimit());
                return BatchJobMapstructTransfer.INSTANCE.schedulePeriodInfoVOSToBatchSchedulePeriodInfoResultVOs(schedulePeriodInfoVOS);
            }
        }.execute();
    }

    @ApiOperation(value = "获取工作流节点的父节点和子节点关联信息")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "getRelatedJobs")
    public R<BatchScheduleJobResultVO> getRelatedJobs(@RequestBody BatchJobQueryJobVO vo) {

        return new APITemplate<BatchScheduleJobResultVO>() {
            @Override
            protected BatchScheduleJobResultVO process() throws BizException {
                ScheduleJobVO relatedJobs = batchJobService.getRelatedJobs(vo.getJobId(), BatchJobMapstructTransfer.INSTANCE.queryJobVOToQueryJobDTO(vo));
                return BatchJobMapstructTransfer.INSTANCE.scheduleJobVOToBatchScheduleJobResultVO(relatedJobs);
            }
        }.execute();
    }

    @ApiOperation(value = "获取任务的状态统计信息")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "queryJobsStatusStatistics")
    public R<Map> queryJobsStatusStatistics(@RequestBody BatchJobQueryJobsStatusStatisticsVO vo) {

        return new APITemplate<Map>() {
            @Override
            protected Map process() throws BizException {
                return batchJobService.queryJobsStatusStatistics(BatchJobMapstructTransfer.INSTANCE.JobsStatusStatisticsVOToQueryJobDTO(vo));
            }
        }.execute();
    }


    @ApiOperation(value = "任务详情")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "jobDetail")
    public R<List<BatchScheduleRunDetailResultVO>> jobDetail(@RequestBody BatchJobDetailVO vo) {

        return new APITemplate<List<BatchScheduleRunDetailResultVO>>() {
            @Override
            protected List<BatchScheduleRunDetailResultVO> process() throws BizException {
                List<ScheduleRunDetailVO> scheduleRunDetailVOS = batchJobService.jobDetail(vo.getTaskId());
                return BatchJobMapstructTransfer.INSTANCE.scheduleRunDetailVOSToBatchScheduleRunDetailResultVOs(scheduleRunDetailVOS);
            }
        }.execute();
    }

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

    @ApiOperation(value = "按业务日期批量停止任务实例")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "stopJobByCondition")
    public R<String> stopJobByCondition(@RequestBody BatchJobKillJobVO vo) {

        return new APITemplate<String>() {

            @Override
            protected String process() throws BizException {
                return batchJobService.stopJobByCondition(vo.getDtuicTenantId(), BatchJobMapstructTransfer.INSTANCE.stopJobByConditionVOToKillJobVo(vo), vo.getUserId(), vo.getIsRoot());
            }
        }.execute();
    }

    @ApiOperation(value = "停止填充数据实例")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "stopFillDataJobs")
    public R<Void> stopFillDataJobs(@RequestBody BatchJobStopFillDataJobsVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                batchJobService.stopFillDataJobs(vo.getFillDataJobName(), vo.getProjectId(), vo.getDtuicTenantId());
                return null;
            }
        }.execute();
    }


    @ApiOperation(value = "批量停止任务")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "batchStopJobs")
    public R<Integer> batchStopJobs(@RequestBody BatchJobStopJobsVO vo) {

        return new APITemplate<Integer>() {
            @Override
            protected Integer process() throws BizException {
                return batchJobService.batchStopJobs(vo.getJobIdList(), vo.getUserId(), vo.getProjectId(), vo.getDtuicTenantId(), vo.getIsRoot());
            }
        }.execute();
    }


    @ApiOperation(value = "补数据")
    @Security(code = AuthCode.MAINTENANCE_BATCHTASKMANAGER_FILLDATA)
    @PostMapping(value = "fillTaskData")
    public R<String> fillTaskData(@RequestBody(required = false) BatchJobFillTaskDataVO vo) {

        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return batchJobService.fillTaskData(vo.getTaskJson(), vo.getFillName(), vo.getFromDay(), vo.getToDay(), vo.getConcreteStartTime(), vo.getConcreteEndTime(), vo.getProjectId(), vo.getUserId(), vo.getTenantId(), vo.getIsRoot(), vo.getDtuicTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "先查询出所有的补数据名称")
    @Security(code = AuthCode.MAINTENANCE_BATCHTASKMANAGER_QUERY)
    @PostMapping(value = "getFillDataJobInfoPreview")
    public R<PageResult<List<ScheduleFillDataJobPreViewResultVO>>> getFillDataJobInfoPreview(@RequestBody(required = false) BatchJobGetFillDataJobInfoPreviewVO vo) {

        return new APITemplate<PageResult<List<ScheduleFillDataJobPreViewResultVO>>>() {
            @Override
            protected PageResult<List<ScheduleFillDataJobPreViewResultVO>> process() throws BizException {
                PageResult<List<ScheduleFillDataJobPreViewVO>> fillDataJobInfoPreview = batchJobService.getFillDataJobInfoPreview(vo.getJobName(), vo.getRunDay(), vo.getBizStartDay(), vo.getBizEndDay(), vo.getDutyUserId(), vo.getProjectId(), vo.getUserId(), vo.getBizDay(), vo.getCurrentPage(), vo.getPageSize(), vo.getTenantId());
                return BatchJobMapstructTransfer.INSTANCE.pageScheduleFillDataJobPreViewVOToScheduleFillDataJobPreViewResultVO(fillDataJobInfoPreview);
            }
        }.execute();
    }

    @ApiOperation(value = "获取补数据详情")
    @Security(code = AuthCode.MAINTENANCE_BATCHTASKMANAGER_QUERY)
    @PostMapping(value = "getFillDataDetailInfo")
    public R<PageResult<BatchScheduleFillDataJobDetailResultVO>> getFillDataDetailInfo(@RequestBody BatchJobGetFillDataDetailInfoVO vo) {

        return new APITemplate<PageResult<BatchScheduleFillDataJobDetailResultVO>>() {
            @Override
            protected PageResult<BatchScheduleFillDataJobDetailResultVO> process() throws BizException {
                PageResult<ScheduleFillDataJobDetailVO> fillDataDetailInfo = batchJobService.getFillDataDetailInfo(BatchJobMapstructTransfer.INSTANCE.jobGetFillDataDetailInfoVOToQueryJobDTO(vo), vo.getFlowJobIdList(), vo.getFillJobName(), vo.getDutyUserId(), vo.getSearchType());
                return BatchJobMapstructTransfer.INSTANCE.scheduleFillDataJobDetailVOToBatchScheduleFillDataJobDetailResultVO(fillDataDetailInfo);
            }
        }.execute();
    }

    @ApiOperation(value = "获取补数据实例工作流节点的父节点和子节点关联信息")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    @PostMapping(value = "getRelatedJobsForFillData")
    public R<BatchFillDataRecordResultVO> getRelatedJobsForFillData(@RequestBody BatchJobQueryJobVO vo) {

        return new APITemplate<BatchFillDataRecordResultVO>() {
            @Override
            protected BatchFillDataRecordResultVO process() throws BizException {
                ScheduleFillDataJobDetailVO.FillDataRecord relatedJobsForFillData = batchJobService.getRelatedJobsForFillData(vo.getJobId(), BatchJobMapstructTransfer.INSTANCE.queryJobVOToQueryJobDTO(vo), vo.getFillJobName());
                return BatchJobMapstructTransfer.INSTANCE.fillDataRecordToBatchFillDataRecordResultVO(relatedJobsForFillData);
            }
        }.execute();
    }

    @ApiOperation(value = "重跑并恢复调度")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "restartJobAndResume")
    public R<Long> restartJobAndResume(@RequestBody BatchRestartJobAndResumeVO vo) {

        return new APITemplate<Long>() {
            @Override
            protected Long process() throws BizException {
                return batchJobService.restartJobAndResume(vo.getJobId(), vo.getJustRunChild(), vo.getSetSuccess(), vo.getSubJobIds());
            }
        }.execute();
    }

    @ApiOperation(value = "批量重新启动")
    @Security(code = AuthCode.MAINTENANCE_BATCH_TASKOP)
    @PostMapping(value = "batchRestartJobAndResume")
    public R<BatchOperatorResultVO> batchRestartJobAndResume(@RequestBody BatchJobBatchRestartJobAndResumeVO vo) {
        return new APITemplate<BatchOperatorResultVO>() {
            @Override
            protected BatchOperatorResultVO process() throws BizException {
                BatchOperatorVO batchOperatorVO = batchJobService.batchRestartJobAndResume(vo.getJobIdList(), vo.getRunCurrentJob());
                return BatchJobMapstructTransfer.INSTANCE.batchOperatorVOToBatchOperatorResultVO(batchOperatorVO);
            }
        }.execute();
    }

    @ApiOperation(value = "获取重跑的数据节点信息")
    @PostMapping(value = "getRestartChildJob")
    public R<List<BatchRestartJobResultVO>> getRestartChildJob(@RequestBody BatchJobGetRestartChildJobVO vo) {

        return new APITemplate<List<BatchRestartJobResultVO>>() {
            @Override
            protected List<BatchRestartJobResultVO> process() throws BizException {
                List<RestartJobVO> restartChildJob = batchJobService.getRestartChildJob(BatchJobMapstructTransfer.INSTANCE.jobGetRestartChildJobVOToScheduleJob(vo), vo.getIsOnlyNextChild());
                return BatchJobMapstructTransfer.INSTANCE.restartJobVOSToBatchRestartJobResultVOs(restartChildJob);
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
                return batchJobService.startSyncImmediately(vo.getTaskId(), vo.getUserId(), vo.getIsRoot(), vo.getDtuicTenantId(), vo.getTaskParams());
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

    @ApiOperation(value = "迁移对应的task 任务信息到 调度")
    @PostMapping(value = "previousJobData")
    public R<Void> previousJobData() {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                batchJobService.previousJobData();
                return null;
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
                return BatchJobMapstructTransfer.INSTANCE.scheduleDetailsVOToBatchJobFindTaskRuleJobResultVO(batchJobService.findTaskRuleJob(vo.getJobId()));
            }
        }.execute();
    }

}
