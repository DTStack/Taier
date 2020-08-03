package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.master.impl.ScheduleJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/scheduleJob")
@Api(value = "/node/scheduleJob", tags = {"任务实例接口"})
public class ScheduleJobController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @RequestMapping(value="/getJobById", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务id展示任务详情")
    public ScheduleJob getJobById(@DtRequestParam("jobId") long jobId) {
        return scheduleJobService.getJobById(jobId);
    }


    @RequestMapping(value="/getStatusJobList", method = {RequestMethod.POST})
    public PageResult getStatusJobList(@DtRequestParam("projectId") Long projectId, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("appType") Integer appType,
                                       @DtRequestParam("dtuicTenantId") Long dtuicTenantId, @DtRequestParam("status") Integer status, @DtRequestParam("pageSize") int pageSize, @DtRequestParam("pageIndex") int pageIndex) {
        return scheduleJobService.getStatusJobList(projectId, tenantId, appType, dtuicTenantId, status, pageSize, pageIndex);
    }

    @RequestMapping(value="/getStatusCount", method = {RequestMethod.POST})
    @ApiOperation(value = "获取各个状态任务的数量")
    public ScheduleJobStatusVO getStatusCount(@DtRequestParam("projectId") Long projectId, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("appType") Integer appType, @DtRequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.getStatusCount(projectId, tenantId, appType, dtuicTenantId);
    }

    @RequestMapping(value="/runTimeTopOrder", method = {RequestMethod.POST})
    @ApiOperation(value = "运行时长top排序")
    public List<JobTopOrderVO> runTimeTopOrder(@DtRequestParam(value = "projectId", required = false) Long projectId,
                                               @DtRequestParam(value = "startTime") Long startTime,
                                               @DtRequestParam("endTime") Long endTime, @DtRequestParam(value = "appType", required = false) Integer appType, @DtRequestParam(value = "dtuicTenantId", required = false) Long dtuicTenantId) {
        return scheduleJobService.runTimeTopOrder(projectId, startTime, endTime, appType, dtuicTenantId);
    }


    @RequestMapping(value="/errorTopOrder", method = {RequestMethod.POST})
    @ApiOperation(value = "近30天任务出错排行")
    public List<JobTopErrorVO> errorTopOrder(@DtRequestParam("projectId") Long projectId, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("appType") Integer appType, @DtRequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.errorTopOrder(projectId, tenantId, appType, dtuicTenantId);
    }


    @RequestMapping(value="/getJobGraph", method = {RequestMethod.POST})
    @ApiOperation(value = "曲线图数据")
    public ScheduleJobChartVO getJobGraph(@DtRequestParam("projectId") Long projectId, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("appType") Integer appType, @DtRequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.getJobGraph(projectId, tenantId, appType, dtuicTenantId);
    }


    @RequestMapping(value="/getScienceJobGraph", method = {RequestMethod.POST})
    @ApiOperation(value = "获取数据科学的曲线图")
    public ChartDataVO getScienceJobGraph(@DtRequestParam("projectId") long projectId, @DtRequestParam("tenantId") Long tenantId,
                                          @DtRequestParam("taskType") String taskType) {
        return scheduleJobService.getScienceJobGraph(projectId, tenantId, taskType);
    }

    @RequestMapping(value="/countScienceJobStatus", method = {RequestMethod.POST})
    public ScheduleJobScienceJobStatusVO countScienceJobStatus(@DtRequestParam("projectIds") List<Long> projectIds, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("runStatus") Integer runStatus, @DtRequestParam("type") Integer type, @DtRequestParam("taskType") String taskType,
                                                               @DtRequestParam("cycStartDay") String cycStartTime, @DtRequestParam("cycEndDay") String cycEndTime) {
        return scheduleJobService.countScienceJobStatus(projectIds, tenantId, runStatus, type, taskType, cycStartTime, cycEndTime);
    }


    @RequestMapping(value="/queryJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "任务运维 - 搜索")
    public PageResult<List<ScheduleJobVO>> queryJobs(@RequestBody QueryJobDTO vo) throws Exception {
        return scheduleJobService.queryJobs(vo);
    }

    @RequestMapping(value="/displayPeriods", method = {RequestMethod.POST})
    public List<SchedulePeriodInfoVO> displayPeriods(@DtRequestParam("isAfter") boolean isAfter, @DtRequestParam("jobId") Long jobId, @DtRequestParam("projectId") Long projectId, @DtRequestParam("limit") int limit) throws Exception {
        return scheduleJobService.displayPeriods(isAfter, jobId, projectId, limit);
    }


    @RequestMapping(value="/getRelatedJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "获取工作流节点的父节点和子节点关联信息")
    public ScheduleJobVO getRelatedJobs(@DtRequestParam("jobId") String jobId, @DtRequestParam("vo") String query) throws Exception {
        return scheduleJobService.getRelatedJobs(jobId, query);
    }

    @RequestMapping(value="/queryJobsStatusStatistics", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务的状态统计信息")
    public Map<String, Long> queryJobsStatusStatistics(@RequestBody QueryJobDTO vo) {
        return scheduleJobService.queryJobsStatusStatistics(vo);
    }

    @RequestMapping(value="/jobDetail", method = {RequestMethod.POST})
    public List<ScheduleRunDetailVO> jobDetail(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType) {
        return scheduleJobService.jobDetail(taskId, appType);
    }

    @RequestMapping(value="/sendTaskStartTrigger", method = {RequestMethod.POST})
    @ApiOperation(value = "触发 engine 执行指定task")
    public void sendTaskStartTrigger(@RequestBody ScheduleJob scheduleJob) throws Exception {
        scheduleJobService.sendTaskStartTrigger(scheduleJob);
    }

    @RequestMapping(value="/stopJob", method = {RequestMethod.POST})
    public String stopJob(@DtRequestParam("jobId") long jobId, @DtRequestParam("userId") Long userId, @DtRequestParam("projectId") Long projectId, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                          @DtRequestParam("isRoot") Boolean isRoot, @DtRequestParam("appType") Integer appType) throws Exception {
        return scheduleJobService.stopJob(jobId, userId, projectId, tenantId, dtuicTenantId, isRoot, appType);
    }


    @RequestMapping(value="/stopFillDataJobs", method = {RequestMethod.POST})
    public void stopFillDataJobs(@DtRequestParam("fillDataJobName") String fillDataJobName, @DtRequestParam("projectId") Long projectId, @DtRequestParam("dtuicTenantId") Long dtuicTenantId, @DtRequestParam("appType") Integer appType) throws Exception {
        scheduleJobService.stopFillDataJobs(fillDataJobName, projectId, dtuicTenantId, appType);
    }


    @RequestMapping(value="/batchStopJobs", method = {RequestMethod.POST})
    public int batchStopJobs(@DtRequestParam("jobIdList") List<Long> jobIdList,
                             @DtRequestParam("projectId") Long projectId,
                             @DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                             @DtRequestParam("appType") Integer appType) {
        return scheduleJobService.batchStopJobs(jobIdList, projectId, dtuicTenantId, appType);
    }


    @RequestMapping(value="/fillTaskData", method = {RequestMethod.POST})
    @ApiOperation(value = "补数据")
    public String fillTaskData(@DtRequestParam("taskJson") String taskJsonStr, @DtRequestParam("fillName") String fillName,
                               @DtRequestParam("fromDay") Long fromDay, @DtRequestParam("toDay") Long toDay,
                               @DtRequestParam("concreteStartTime") String beginTime, @DtRequestParam("concreteEndTime") String endTime,
                               @DtRequestParam("projectId") Long projectId, @DtRequestParam("userId") Long userId,
                               @DtRequestParam("tenantId") Long tenantId,
                               @DtRequestParam("isRoot") Boolean isRoot, @DtRequestParam("appType") Integer appType, @DtRequestParam("dtuicTenantId") Long dtuicTenantId) throws Exception {
        return scheduleJobService.fillTaskData(taskJsonStr, fillName, fromDay, toDay, beginTime, endTime, projectId, userId, tenantId, isRoot, appType, dtuicTenantId);
    }


    @RequestMapping(value="/getFillDataJobInfoPreview", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobPreViewVO> getFillDataJobInfoPreview(@DtRequestParam("jobName") String jobName, @DtRequestParam("runDay") Long runDay,
                                                                              @DtRequestParam("bizStartDay") Long bizStartDay, @DtRequestParam("bizEndDay") Long bizEndDay, @DtRequestParam("dutyUserId") Long dutyUserId,
                                                                              @DtRequestParam("projectId") Long projectId, @DtRequestParam("appType") Integer appType, @DtRequestParam("user") Integer userId,
                                                                              @DtRequestParam("currentPage") Integer currentPage, @DtRequestParam("pageSize") Integer pageSize, @DtRequestParam("tenantId") Long tenantId) {
        return scheduleJobService.getFillDataJobInfoPreview(jobName, runDay, bizStartDay, bizEndDay, dutyUserId, projectId, appType, userId, currentPage, pageSize, tenantId);
    }


    @Deprecated
    @RequestMapping(value="/getFillDataDetailInfoOld", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(@RequestBody QueryJobDTO vo,
                                                                            @RequestParam("fillJobName") String fillJobName,
                                                                            @RequestParam("dutyUserId") Long dutyUserId) throws Exception {
        return scheduleJobService.getFillDataDetailInfoOld(vo, fillJobName, dutyUserId);
    }

    @RequestMapping(value="/getFillDataDetailInfo", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo(@DtRequestParam("vo") String queryJobDTO,
                                                                         @DtRequestParam("flowJobIdList") List<String> flowJobIdList,
                                                                         @DtRequestParam("fillJobName") String fillJobName,
                                                                         @DtRequestParam("dutyUserId") Long dutyUserId, @DtRequestParam("searchType") String searchType,
                                                                         @DtRequestParam("appType") Integer appType) throws Exception {
        return scheduleJobService.getFillDataDetailInfo(queryJobDTO, flowJobIdList, fillJobName, dutyUserId, searchType, appType);
    }


    @RequestMapping(value="/getRelatedJobsForFillData", method = {RequestMethod.POST})
    @ApiOperation(value = "获取补数据实例工作流节点的父节点和子节点关联信息")
    public ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData(@DtRequestParam("jobId") String jobId, @DtRequestParam("vo") String query,
                                                                                @DtRequestParam("fillJobName") String fillJobName) throws Exception {
        return scheduleJobService.getRelatedJobsForFillData(jobId, query, fillJobName);
    }


    @RequestMapping(value="/getRestartChildJob", method = {RequestMethod.POST})
    @ApiOperation(value = "获取重跑的数据节点信息")
    public List<RestartJobVO> getRestartChildJob(@DtRequestParam("jobKey") String jobKey, @DtRequestParam("taskId") Long parentTaskId, @DtRequestParam("isOnlyNextChild") boolean isOnlyNextChild) {
        return scheduleJobService.getRestartChildJob(jobKey, parentTaskId, isOnlyNextChild);
    }

    @RequestMapping(value="/listJobIdByTaskNameAndStatusList", method = {RequestMethod.POST})
    public List<String> listJobIdByTaskNameAndStatusList(@DtRequestParam("taskName") String taskName, @DtRequestParam("statusList") List<Integer> statusList, @DtRequestParam("projectId") Long projectId,@DtRequestParam("appType") Integer appType) {
        return scheduleJobService.listJobIdByTaskNameAndStatusList(taskName, statusList, projectId, appType);
    }


    @RequestMapping(value="/getLabTaskRelationMap", method = {RequestMethod.POST})
    @ApiOperation(value = "返回这些jobId对应的父节点的jobMap")
    public Map<String, ScheduleJob> getLabTaskRelationMap(@DtRequestParam("jobIdList") List<String> jobIdList, @DtRequestParam("projectId") Long projectId) {
        return scheduleJobService.getLabTaskRelationMap(jobIdList, projectId);
    }


    @RequestMapping(value="/statisticsTaskRecentInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务执行信息")
    public List<Map<String, Object>> statisticsTaskRecentInfo(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("projectId") Long projectId, @DtRequestParam("count") Integer count) {
        return scheduleJobService.statisticsTaskRecentInfo(taskId, appType, projectId, count);
    }


    @RequestMapping(value="/BatchJobsBatchUpdate", method = {RequestMethod.POST})
    @ApiOperation(value = "批量更新")
    public Integer BatchJobsBatchUpdate(@DtRequestParam("jobs") String jobs) {
        return scheduleJobService.BatchJobsBatchUpdate(jobs);
    }

    @RequestMapping(value="/updateTimeNull", method = {RequestMethod.POST})
    @ApiOperation(value = "把开始时间和结束时间置为null")
    public Integer updateTimeNull(@DtRequestParam("jobId") String jobId) {
        return scheduleJobService.updateTimeNull(jobId);
    }


    @RequestMapping(value="/getById", method = {RequestMethod.POST})
    public ScheduleJob getById(@DtRequestParam("id") Long id) {
        return scheduleJobService.getById(id);
    }

    @RequestMapping(value="/getByJobId", method = {RequestMethod.POST})
    public ScheduleJob getByJobId(@DtRequestParam("jobId") String jobId, @DtRequestParam("isDeleted") Integer isDeleted) {
        return scheduleJobService.getByJobId(jobId, isDeleted);
    }

    @RequestMapping(value="/getByIds", method = {RequestMethod.POST})
    public List<ScheduleJob> getByIds(@DtRequestParam("ids") List<Long> ids, @DtRequestParam("project") Long projectId) {
        return scheduleJobService.getByIds(ids, projectId);
    }


    @RequestMapping(value="/getSameDayChildJob", method = {RequestMethod.POST})
    @ApiOperation(value = "离线调用")
    public List<ScheduleJob> getSameDayChildJob(@DtRequestParam("batchJob") String batchJob,
                                                @DtRequestParam("isOnlyNextChild") boolean isOnlyNextChild, @DtRequestParam("appType") Integer appType) {
        return scheduleJobService.getSameDayChildJob(batchJob, isOnlyNextChild, appType);
    }

    @RequestMapping(value="/getAllChildJobWithSameDay", method = {RequestMethod.POST})
    @ApiOperation(value = "查询出指定job的所有关联的子job")
    public List<ScheduleJob> getAllChildJobWithSameDay(@RequestBody ScheduleJob scheduleJob,
                                                       @RequestParam("isOnlyNextChild") boolean isOnlyNextChild, @RequestParam("appType") Integer appType) {
        return scheduleJobService.getAllChildJobWithSameDay(scheduleJob, isOnlyNextChild, appType);
    }


    @RequestMapping(value="/generalCount", method = {RequestMethod.POST})
    public Integer generalCount(@RequestBody ScheduleJobDTO query) {
        return scheduleJobService.generalCount(query);
    }

    @RequestMapping(value="/generalCountWithMinAndHour", method = {RequestMethod.POST})
    public Integer generalCountWithMinAndHour(@RequestBody ScheduleJobDTO query) {
        return scheduleJobService.generalCountWithMinAndHour(query);
    }

    @RequestMapping(value="/generalQuery", method = {RequestMethod.POST})
    public List<ScheduleJob> generalQuery(@RequestBody PageQuery query) {
        return scheduleJobService.generalQuery(query);
    }

    @RequestMapping(value="/generalQueryWithMinAndHour", method = {RequestMethod.POST})
    public List<ScheduleJob> generalQueryWithMinAndHour(@RequestBody PageQuery query) {
        return scheduleJobService.generalQueryWithMinAndHour(query);
    }

    @RequestMapping(value="/getLastSuccessJob", method = {RequestMethod.POST})
    @ApiOperation(value = "获取job最后一次执行")
    public ScheduleJob getLastSuccessJob(@DtRequestParam("taskId") Long taskId, @DtRequestParam("time") Timestamp time, @DtRequestParam("appType") Integer appType) {
        return scheduleJobService.getLastSuccessJob(taskId, time, appType);
    }


    @RequestMapping(value="/setAlogrithmLabLog", method = {RequestMethod.POST})
    @ApiOperation(value = "设置算法实验日志, 获取全部子节点日志")
    public ScheduleServerLogVO setAlogrithmLabLog(@DtRequestParam("status") Integer status, @DtRequestParam("taskType") Integer taskType, @DtRequestParam("jobId") String jobId,
                                                  @DtRequestParam("info") String info, @DtRequestParam("logVo") String logVo, @DtRequestParam("appType") Integer appType) throws Exception {
        return scheduleJobService.setAlogrithmLabLog(status, taskType, jobId, info, logVo, appType);
    }


    @RequestMapping(value="/minOrHourJobQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "周期实例列表, 分钟任务和小时任务 展开按钮显示")
    public List<ScheduleJobVO> minOrHourJobQuery(@RequestBody ScheduleJobDTO scheduleJobDTO) {
        return scheduleJobService.minOrHourJobQuery(scheduleJobDTO);
    }

    @RequestMapping(value="/updateJobStatusAndLogInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "更新任务状态和日志")
    public void updateJobStatusAndLogInfo(@DtRequestParam("jobId") String jobId, @DtRequestParam("status") Integer status, @DtRequestParam("logInfo") String logInfo) {
        scheduleJobService.updateJobStatusAndLogInfo(jobId, status, logInfo);
    }


    @RequestMapping(value="/testCheckCanRun", method = {RequestMethod.POST})
    @ApiOperation(value = "测试任务 是否可以运行")
    public String testCheckCanRun(@DtRequestParam("jobId")String jobId) {
        return scheduleJobService.testCheckCanRun(jobId);
    }

    @RequestMapping(value="/createTodayTaskShade", method = {RequestMethod.POST})
    @ApiOperation(value = "生成当天任务实例")
    public void createTodayTaskShade(@DtRequestParam("taskId") Long taskId,@DtRequestParam("appType") Integer appType) {
        scheduleJobService.createTodayTaskShade(taskId, appType);
    }

    @RequestMapping(value="/listByBusinessDateAndPeriodTypeAndStatusList", method = {RequestMethod.POST})
    public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(@RequestBody ScheduleJobDTO query) {
        return scheduleJobService.listByBusinessDateAndPeriodTypeAndStatusList(query);
    }

    @RequestMapping(value="/listByCyctimeAndJobName", method = {RequestMethod.POST})
    @ApiOperation(value = "根据cycTime和jobName获取，如获取当天的周期实例任务")
    public List<ScheduleJob> listByCyctimeAndJobName(@DtRequestParam("preCycTime") String preCycTime, @DtRequestParam("preJobName") String preJobName, @DtRequestParam("scheduleType") Integer scheduleType) {
        return scheduleJobService.listByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    @RequestMapping(value="/listByCyctimeAndJobNameWithStartId", method = {RequestMethod.POST})
    @ApiOperation(value = "按批次根据cycTime和jobName获取，如获取当天的周期实例任务")
    public List<ScheduleJob> listByCyctimeAndJobName(@DtRequestParam("startId") Long startId, @DtRequestParam("preCycTime") String preCycTime, @DtRequestParam("preJobName") String preJobName, @DtRequestParam("scheduleType") Integer scheduleType, @DtRequestParam("batchJobSize") Integer batchJobSize) {
        return scheduleJobService.listByCyctimeAndJobName(startId, preCycTime, preJobName, scheduleType, batchJobSize);
    }

    @RequestMapping(value="/countByCyctimeAndJobName", method = {RequestMethod.POST})
    public Integer countByCyctimeAndJobName(@DtRequestParam("preCycTime") String preCycTime, @DtRequestParam("preJobName") String preJobName, @DtRequestParam("scheduleType") Integer scheduleType) {
        return scheduleJobService.countByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    @RequestMapping(value="/deleteJobsByJobKey", method = {RequestMethod.POST})
    @ApiOperation(value = "根据jobKey删除job jobjob记录")
    public void deleteJobsByJobKey(@DtRequestParam("jobKeyList") List<String> jobKeyList) {
        scheduleJobService.deleteJobsByJobKey(jobKeyList);
    }


    @RequestMapping(value="/syncBatchJob", method = {RequestMethod.POST})
    public List<ScheduleJob> syncBatchJob(@RequestBody QueryJobDTO dto) {
        return scheduleJobService.syncBatchJob(dto);
    }

    @RequestMapping(value="/listJobsByTaskIdsAndApptype", method = {RequestMethod.POST})
    @ApiOperation(value = "根据taskId、appType 拿到对应的job集合")
    public List<ScheduleJob> listJobsByTaskIdsAndApptype(@DtRequestParam("taskIds") List<Long> taskIds,@DtRequestParam("appType") Integer appType) {
        return scheduleJobService.listJobsByTaskIdsAndApptype(taskIds, appType);
    }

    @RequestMapping(value="/stopJobByJobId", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务ID 停止任务")
    public String stopJobByJobId(@DtRequestParam("jobId") String jobId, @DtRequestParam("userId") Long userId, @DtRequestParam("projectId") Long projectId, @DtRequestParam("tenantId") Long tenantId, @DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                          @DtRequestParam("isRoot") Boolean isRoot, @DtRequestParam("appType") Integer appType) throws Exception {
        return scheduleJobService.stopJobByJobId(jobId, userId, projectId, tenantId, dtuicTenantId, isRoot, appType);
    }

    @RequestMapping(value="/buildTaskJobGraphTest", method = {RequestMethod.POST})
    @ApiOperation(value = "生成指定日期的周期实例(需要数据库无对应记录)")
    public void buildTaskJobGraphTest(@DtRequestParam("triggerDay") String triggerDay) {
        scheduleJobService.buildTaskJobGraphTest(triggerDay);
    }

    @RequestMapping(value="/testTrigger", method = {RequestMethod.POST})
    public void testTrigger(@DtRequestParam("jobId") String jobId) {
        scheduleJobService.testTrigger(jobId);
    }
}
