package com.dtstack.engine.master.controller;

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.dto.QueryJobDTO;
import com.dtstack.engine.dto.ScheduleJobDTO;
import com.dtstack.engine.common.pager.PageQuery;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.vo.*;
import com.dtstack.engine.master.vo.schedule.job.ScheduleJobRuleTimeVO;
import com.dtstack.engine.master.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.master.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EnvironmentContext context;

    @RequestMapping(value = "/getJobById", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务id展示任务详情")
    public ScheduleJob getJobById(@RequestParam("jobId") long jobId) {
        return scheduleJobService.getJobById(jobId);
    }


    @RequestMapping(value = "/getStatusJobList", method = {RequestMethod.POST})
    public PageResult getStatusJobList(@RequestParam("projectId") Long projectId, @RequestParam("tenantId") Long tenantId, @RequestParam("appType") Integer appType,
                                       @RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("status") Integer status, @RequestParam("pageSize") int pageSize, @RequestParam("pageIndex") int pageIndex) {
        return scheduleJobService.getStatusJobList(projectId, tenantId, appType, dtuicTenantId, status, pageSize, pageIndex);
    }

    @RequestMapping(value = "/getStatusCount", method = {RequestMethod.POST})
    @ApiOperation(value = "获取各个状态任务的数量")
    public ScheduleJobStatusVO getStatusCount(@RequestParam("projectId") Long projectId, @RequestParam("tenantId") Long tenantId, @RequestParam("appType") Integer appType, @RequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.getStatusCount(projectId, tenantId, appType, dtuicTenantId);
    }

    @RequestMapping(value = "/getStatusCountByProjectIds", method = {RequestMethod.POST})
    @ApiOperation(value = "获取各个状态任务的数量")
    public List<ScheduleJobStatusVO> getStatusCountByProjectIds(@RequestParam("projectIds") List<Long> projectIds, @RequestParam("tenantId") Long tenantId, @RequestParam("appType") Integer appType, @RequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.getStatusCountByProjectIds(projectIds, tenantId, appType, dtuicTenantId);
    }

    @RequestMapping(value = "/runTimeTopOrder", method = {RequestMethod.POST})
    @ApiOperation(value = "运行时长top排序")
    public List<JobTopOrderVO> runTimeTopOrder(@RequestParam(value = "projectId") Long projectId,
                                               @RequestParam(value = "startTime") Long startTime,
                                               @RequestParam("endTime") Long endTime, @RequestParam(value = "appType", required = false) Integer appType, @RequestParam(value = "dtuicTenantId", required = false) Long dtuicTenantId) {
        return scheduleJobService.runTimeTopOrder(projectId, startTime, endTime, appType, dtuicTenantId);
    }


    @RequestMapping(value = "/errorTopOrder", method = {RequestMethod.POST})
    @ApiOperation(value = "近30天任务出错排行")
    public List<JobTopErrorVO> errorTopOrder(@RequestParam("projectId") Long projectId, @RequestParam("tenantId") Long tenantId, @RequestParam("appType") Integer appType, @RequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.errorTopOrder(projectId, tenantId, appType, dtuicTenantId);
    }


    @RequestMapping(value = "/getJobGraph", method = {RequestMethod.POST})
    @ApiOperation(value = "曲线图数据")
    public ScheduleJobChartVO getJobGraph(@RequestParam("projectId") Long projectId, @RequestParam("tenantId") Long tenantId, @RequestParam("appType") Integer appType, @RequestParam("dtuicTenantId") Long dtuicTenantId) {
        return scheduleJobService.getJobGraph(projectId, tenantId, appType, dtuicTenantId);
    }


    @RequestMapping(value = "/getScienceJobGraph", method = {RequestMethod.POST})
    @ApiOperation(value = "获取数据科学的曲线图")
    public ChartDataVO getScienceJobGraph(@RequestParam("projectId") long projectId, @RequestParam("tenantId") Long tenantId,
                                          @RequestParam("taskType") String taskType) {
        return scheduleJobService.getScienceJobGraph(projectId, tenantId, taskType);
    }

    @RequestMapping(value = "/countScienceJobStatus", method = {RequestMethod.POST})
    public ScheduleJobScienceJobStatusVO countScienceJobStatus(@RequestParam("projectIds") List<Long> projectIds, @RequestParam("tenantId") Long tenantId, @RequestParam("runStatus") Integer runStatus, @RequestParam("type") Integer type, @RequestParam("taskType") String taskType,
                                                               @RequestParam("cycStartDay") String cycStartTime, @RequestParam("cycEndDay") String cycEndTime) {
        return scheduleJobService.countScienceJobStatus(projectIds, tenantId, runStatus, type, taskType, cycStartTime, cycEndTime);
    }


    @RequestMapping(value = "/queryJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "任务运维 - 搜索")
    public PageResult<List<ScheduleJobVO>> queryJobs(@RequestBody QueryJobDTO vo) throws Exception {
        return scheduleJobService.queryJobs(vo);
    }

    @RequestMapping(value = "/displayPeriods", method = {RequestMethod.POST})
    public List<SchedulePeriodInfoVO> displayPeriods(@RequestParam("isAfter") boolean isAfter, @RequestParam("jobId") Long jobId, @RequestParam("projectId") Long projectId, @RequestParam("limit") int limit) throws Exception {
        return scheduleJobService.displayPeriods(isAfter, jobId, projectId, limit);
    }


    @RequestMapping(value = "/getRelatedJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "获取工作流节点的父节点和子节点关联信息")
    public ScheduleJobVO getRelatedJobs(@RequestParam("jobId") String jobId, @RequestParam("vo") String query) throws Exception {
        return scheduleJobService.getRelatedJobs(jobId, query);
    }

    @RequestMapping(value = "/queryJobsStatusStatistics", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务的状态统计信息")
    public Map<String, Long> queryJobsStatusStatistics(@RequestBody QueryJobDTO vo) {
        return scheduleJobService.queryJobsStatusStatistics(vo);
    }

    @RequestMapping(value = "/jobDetail", method = {RequestMethod.POST})
    public List<ScheduleRunDetailVO> jobDetail(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType) {
        return scheduleJobService.jobDetail(taskId, appType);
    }

    @RequestMapping(value = "/sendTaskStartTrigger", method = {RequestMethod.POST})
    @ApiOperation(value = "触发 engine 执行指定task")
    public void sendTaskStartTrigger(@RequestBody ScheduleJob scheduleJob) throws Exception {
        scheduleJobService.sendTaskStartTrigger(scheduleJob);
    }

    @RequestMapping(value = "/stopJob", method = {RequestMethod.POST})
    public void stopJob(@RequestParam("jobId") Long jobId,
                          @RequestParam("appType") Integer appType) throws Exception {
        scheduleJobService.stopJob(jobId, appType);
    }


    @RequestMapping(value = "/stopFillDataJobs", method = {RequestMethod.POST})
    public void stopFillDataJobs(@RequestParam("fillDataJobName") String fillDataJobName,
                                 @RequestParam("projectId") Long projectId,
                                 @RequestParam("dt_tenant_id") Long dtuicTenantId,
                                 @RequestParam("appType") Integer appType) throws Exception {
        scheduleJobService.stopFillDataJobs(fillDataJobName, projectId, dtuicTenantId, appType);
    }


    @RequestMapping(value = "/batchStopJobs", method = {RequestMethod.POST})
    public int batchStopJobs(@RequestParam("jobIdList") List<Long> jobIdList) {
        return scheduleJobService.batchStopJobs(jobIdList);
    }


    @RequestMapping(value = "/fillTaskData", method = {RequestMethod.POST})
    @ApiOperation(value = "补数据")
    public String fillTaskData(@RequestParam("taskJson") String taskJsonStr,
                               @RequestParam("fillName") String fillName,
                               @RequestParam("fromDay") Long fromDay,
                               @RequestParam("toDay") Long toDay,
                               @RequestParam("concreteStartTime") String beginTime,
                               @RequestParam("concreteEndTime") String endTime,
                               @RequestParam("projectId") Long projectId,
                               @RequestParam("dt_user_id") Long userId,
                               @RequestParam("tenantId") Long tenantId,
                               @RequestParam("isRoot") Boolean isRoot,
                               @RequestParam("appType") Integer appType,
                               @RequestParam("dt_tenant_id") Long dtuicTenantId,
                               @RequestParam("ignoreCycTime") Boolean ignoreCycTime) throws Exception {
        return scheduleJobService.fillTaskData(taskJsonStr, fillName, fromDay, toDay, beginTime, endTime, projectId, userId, tenantId, isRoot, appType, dtuicTenantId,ignoreCycTime);
    }


    @RequestMapping(value = "/getFillDataJobInfoPreview", method = {RequestMethod.POST})
    public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(@RequestParam("jobName") String jobName,
                                                                                    @RequestParam("runDay") Long runDay,
                                                                                    @RequestParam("bizStartDay") Long bizStartDay,
                                                                                    @RequestParam("bizEndDay") Long bizEndDay,
                                                                                    @RequestParam("dt_user_id") Long dutyUserId,
                                                                                    @RequestParam("projectId") Long projectId,
                                                                                    @RequestParam("appType") Integer appType,
                                                                                    @RequestParam("currentPage") Integer currentPage,
                                                                                    @RequestParam("pageSize") Integer pageSize,
                                                                                    @RequestParam("tenantId") Long tenantId,
                                                                                    @RequestParam("dt_tenant_id") Long dtuicTenantId) {
        return scheduleJobService.getFillDataJobInfoPreview(jobName, runDay, bizStartDay, bizEndDay, dutyUserId, projectId, appType, currentPage, pageSize, tenantId,dtuicTenantId);
    }


    @Deprecated
    @RequestMapping(value = "/getFillDataDetailInfoOld", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(@RequestBody QueryJobDTO vo,
                                                                            @RequestParam("fillJobName") String fillJobName,
                                                                            @RequestParam("dutyUserId") Long dutyUserId) throws Exception {
        return scheduleJobService.getFillDataDetailInfoOld(vo, fillJobName, dutyUserId);
    }

    @RequestMapping(value = "/getFillDataDetailInfo", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo(@RequestParam("vo") String queryJobDTO,
                                                                         @RequestParam("flowJobIdList") List<String> flowJobIdList,
                                                                         @RequestParam("fillJobName") String fillJobName,
                                                                         @RequestParam("dutyUserId") Long dutyUserId,
                                                                         @RequestParam("searchType") String searchType,
                                                                         @RequestParam("appType") Integer appType) throws Exception {
        return scheduleJobService.getFillDataDetailInfo(queryJobDTO, flowJobIdList, fillJobName, dutyUserId, searchType, appType);
    }

    @RequestMapping(value = "/getJobGetFillDataDetailInfo", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobDetailVO> getJobGetFillDataDetailInfo(@RequestParam("taskName") String taskName,
                                                                               @RequestParam("bizStartDay") Long bizStartDay,
                                                                               @RequestParam("bizEndDay") Long bizEndDay,
                                                                               @RequestParam("flowJobIdList") List<String> flowJobIdList,
                                                                               @RequestParam("fillJobName") String fillJobName,
                                                                               @RequestParam("dutyUserId") Long dutyUserId,
                                                                               @RequestParam("searchType") String searchType,
                                                                               @RequestParam("appType") Integer appType,
                                                                               @RequestParam("projectId") Long projectId,
                                                                               @RequestParam("dt_tenant_id") Long dtuicTenantId,
                                                                               @RequestParam("execTimeSort") String execTimeSort, @RequestParam("execStartSort") String execStartSort,
                                                                               @RequestParam("execEndSort") String execEndSort, @RequestParam("cycSort") String cycSort,
                                                                               @RequestParam("businessDateSort") String businessDateSort, @RequestParam("retryNumSort") String retryNumSort,
                                                                               @RequestParam("taskType") String taskType,
                                                                               @RequestParam("jobStatuses") String jobStatuses,
                                                                               @RequestParam("currentPage") Integer currentPage,
                                                                               @RequestParam("pageSize") Integer pageSize) throws Exception {
        return scheduleJobService.getJobGetFillDataDetailInfo(taskName, bizStartDay, bizEndDay, flowJobIdList, fillJobName, dutyUserId, searchType, appType, projectId, dtuicTenantId,
                execTimeSort, execStartSort, execEndSort, cycSort, businessDateSort, retryNumSort,taskType, jobStatuses,currentPage, pageSize);
    }




    @RequestMapping(value = "/getRelatedJobsForFillData", method = {RequestMethod.POST})
    @ApiOperation(value = "获取补数据实例工作流节点的父节点和子节点关联信息")
    public ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData(@RequestParam("jobId") String jobId, @RequestParam("vo") String query,
                                                                                @RequestParam("fillJobName") String fillJobName) throws Exception {
        return scheduleJobService.getRelatedJobsForFillData(jobId, query, fillJobName);
    }


    @RequestMapping(value = "/getRestartChildJob", method = {RequestMethod.POST})
    @ApiOperation(value = "获取重跑的数据节点信息")
    public List<RestartJobVO> getRestartChildJob(@RequestParam("jobKey") String jobKey, @RequestParam("taskId") Long parentTaskId, @RequestParam("isOnlyNextChild") boolean isOnlyNextChild) {
        return scheduleJobService.getRestartChildJob(jobKey, parentTaskId, isOnlyNextChild);
    }

    @RequestMapping(value = "/listJobIdByTaskNameAndStatusList", method = {RequestMethod.POST})
    public List<String> listJobIdByTaskNameAndStatusList(@RequestParam("taskName") String taskName, @RequestParam("statusList") List<Integer> statusList, @RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType) {
        return scheduleJobService.listJobIdByTaskNameAndStatusList(taskName, statusList, projectId, appType);
    }


    @RequestMapping(value = "/getLabTaskRelationMap", method = {RequestMethod.POST})
    @ApiOperation(value = "返回这些jobId对应的父节点的jobMap")
    public Map<String, ScheduleJob> getLabTaskRelationMap(@RequestParam("jobIdList") List<String> jobIdList, @RequestParam("projectId") Long projectId) {
        return scheduleJobService.getLabTaskRelationMap(jobIdList, projectId);
    }


    @RequestMapping(value = "/statisticsTaskRecentInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务执行信息")
    public List<Map<String, Object>> statisticsTaskRecentInfo(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId, @RequestParam("count") Integer count) {
        return scheduleJobService.statisticsTaskRecentInfo(taskId, appType, projectId, count);
    }


    @RequestMapping(value = "/BatchJobsBatchUpdate", method = {RequestMethod.POST})
    @ApiOperation(value = "批量更新")
    public Integer BatchJobsBatchUpdate(@RequestParam("jobs") String jobs) {
        return scheduleJobService.BatchJobsBatchUpdate(jobs);
    }

    @RequestMapping(value = "/updateTimeNull", method = {RequestMethod.POST})
    @ApiOperation(value = "把开始时间和结束时间置为null")
    public Integer updateTimeNull(@RequestParam("jobId") String jobId) {
        return scheduleJobService.updateTimeNull(jobId);
    }


    @RequestMapping(value = "/getById", method = {RequestMethod.POST})
    public ScheduleJob getById(@RequestParam("id") Long id) {
        return scheduleJobService.getById(id);
    }

    @RequestMapping(value = "/getByJobId", method = {RequestMethod.POST})
    public ScheduleJob getByJobId(@RequestParam("jobId") String jobId, @RequestParam("isDeleted") Integer isDeleted) {
        return scheduleJobService.getByJobId(jobId, isDeleted);
    }

    @RequestMapping(value = "/getByIds", method = {RequestMethod.POST})
    public List<ScheduleJob> getByIds(@RequestParam("ids") List<Long> ids) {
        return scheduleJobService.getByIds(ids);
    }


    @RequestMapping(value = "/getSameDayChildJob", method = {RequestMethod.POST})
    @ApiOperation(value = "离线计算重跑任务及其下游")
    public List<ScheduleJob> getSameDayChildJob(@RequestParam("batchJob") String batchJob,
                                                @RequestParam("isOnlyNextChild") boolean isOnlyNextChild, @RequestParam("appType") Integer appType) {
        return scheduleJobService.getSameDayChildJob(batchJob, isOnlyNextChild, appType);
    }

    @RequestMapping(value = "/getAllChildJobWithSameDay", method = {RequestMethod.POST})
    @ApiOperation(value = "查询出指定job的所有关联的子job")
    public List<ScheduleJob> getAllChildJobWithSameDay(@RequestBody ScheduleJob scheduleJob,
                                                       @RequestParam("isOnlyNextChild") boolean isOnlyNextChild, @RequestParam("appType") Integer appType) {
        Integer jobLevel = context.getJobJobLevel();
        return scheduleJobService.getAllChildJobWithSameDay(scheduleJob, isOnlyNextChild, appType, jobLevel);
    }


    @RequestMapping(value = "/generalCount", method = {RequestMethod.POST})
    public Integer generalCount(@RequestBody ScheduleJobDTO query) {
        if (query.getBizEndDay() == null || query.getBizStartDay() == null) {
            throw new RdosDefineException("业务时间必须必传");
        }
        return scheduleJobService.generalCount(query);
    }

    @RequestMapping(value = "/generalCountWithMinAndHour", method = {RequestMethod.POST})
    public Integer generalCountWithMinAndHour(@RequestBody ScheduleJobDTO query) {
        return scheduleJobService.generalCountWithMinAndHour(query);
    }

    @RequestMapping(value = "/generalQuery", method = {RequestMethod.POST})
    public List<ScheduleJob> generalQuery(@RequestBody PageQuery query) {
        return scheduleJobService.generalQuery(query);
    }

    @RequestMapping(value = "/generalQueryWithMinAndHour", method = {RequestMethod.POST})
    public List<ScheduleJob> generalQueryWithMinAndHour(@RequestBody PageQuery query) {
        return scheduleJobService.generalQueryWithMinAndHour(query);
    }

    @RequestMapping(value = "/getLastSuccessJob", method = {RequestMethod.POST})
    @ApiOperation(value = "获取job最后一次执行")
    public ScheduleJob getLastSuccessJob(@RequestParam("taskId") Long taskId, @RequestParam("time") Timestamp time, @RequestParam("appType") Integer appType) {
        return scheduleJobService.getLastSuccessJob(taskId, time, appType);
    }


    @RequestMapping(value = "/setAlogrithmLabLog", method = {RequestMethod.POST})
    @ApiOperation(value = "设置算法实验日志, 获取全部子节点日志")
    public ScheduleServerLogVO setAlogrithmLabLog(@RequestParam("status") Integer status, @RequestParam("taskType") Integer taskType, @RequestParam("jobId") String jobId,
                                                  @RequestParam("info") String info, @RequestParam("logVo") String logVo, @RequestParam("appType") Integer appType) throws Exception {
        return scheduleJobService.setAlogrithmLabLog(status, taskType, jobId, info, logVo, appType);
    }


    @RequestMapping(value = "/minOrHourJobQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "周期实例列表, 分钟任务和小时任务 展开按钮显示")
    public List<ScheduleJobVO> minOrHourJobQuery(@RequestBody ScheduleJobDTO scheduleJobDTO) {
        return scheduleJobService.minOrHourJobQuery(scheduleJobDTO);
    }

    @RequestMapping(value = "/updateJobStatusAndLogInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "更新任务状态和日志")
    public void updateJobStatusAndLogInfo(@RequestParam("jobId") String jobId, @RequestParam("status") Integer status, @RequestParam("logInfo") String logInfo) {
        scheduleJobService.updateJobStatusAndLogInfo(jobId, status, logInfo);
    }


    @RequestMapping(value = "/testCheckCanRun", method = {RequestMethod.POST})
    @ApiOperation(value = "测试任务 是否可以运行")
    public String testCheckCanRun(@RequestParam("jobId") String jobId) {
        return scheduleJobService.testCheckCanRun(jobId);
    }

    @RequestMapping(value = "/createTodayTaskShade", method = {RequestMethod.POST})
    @ApiOperation(value = "生成当天任务实例")
    public void createTodayTaskShade(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("date") String date) {
        scheduleJobService.createTodayTaskShade(taskId, appType, date);
    }

    @RequestMapping(value = "/listByBusinessDateAndPeriodTypeAndStatusList", method = {RequestMethod.POST})
    public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(@RequestBody ScheduleJobDTO query) {
        return scheduleJobService.listByBusinessDateAndPeriodTypeAndStatusList(query);
    }

    @RequestMapping(value = "/listByCyctimeAndJobName", method = {RequestMethod.POST})
    @ApiOperation(value = "根据cycTime和jobName获取，如获取当天的周期实例任务")
    public List<ScheduleJob> listByCyctimeAndJobName(@RequestParam("preCycTime") String preCycTime, @RequestParam("preJobName") String preJobName, @RequestParam("scheduleType") Integer scheduleType) {
        return scheduleJobService.listByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    /**
     * @param startId:
     * @param preCycTime:
     * @param preJobName:
     * @param scheduleType:
     * @param batchJobSize:
     * @author newman
     * @Description 该接口目前只有内部使用
     * @Date 2021/1/12 4:27 下午
     * @return: java.util.List<com.dtstack.engine.domain.ScheduleJob>
     **/
    @RequestMapping(value = "/listByCyctimeAndJobNameWithStartId", method = {RequestMethod.POST})
    @ApiOperation(value = "按批次根据cycTime和jobName获取，如获取当天的周期实例任务")
    public List<ScheduleJob> listByCyctimeAndJobName(@RequestParam("startId") Long startId, @RequestParam("preCycTime") String preCycTime, @RequestParam("preJobName") String preJobName, @RequestParam("scheduleType") Integer scheduleType, @RequestParam("batchJobSize") Integer batchJobSize) {
        return scheduleJobService.listByCyctimeAndJobName(startId, preCycTime, preJobName, scheduleType, batchJobSize);
    }

    @RequestMapping(value = "/countByCyctimeAndJobName", method = {RequestMethod.POST})
    public Integer countByCyctimeAndJobName(@RequestParam("preCycTime") String preCycTime, @RequestParam("preJobName") String preJobName, @RequestParam("scheduleType") Integer scheduleType) {
        return scheduleJobService.countByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    @RequestMapping(value = "/deleteJobsByJobKey", method = {RequestMethod.POST})
    @ApiOperation(value = "根据jobKey删除job jobjob记录")
    public void deleteJobsByJobKey(@RequestParam("jobKeyList") List<String> jobKeyList) {
        scheduleJobService.deleteJobsByJobKey(jobKeyList);
    }


    @RequestMapping(value = "/syncBatchJob", method = {RequestMethod.POST})
    public List<ScheduleJob> syncBatchJob(@RequestBody QueryJobDTO dto) {
        return scheduleJobService.syncBatchJob(dto);
    }

    @RequestMapping(value = "/listJobsByTaskIdsAndApptype", method = {RequestMethod.POST})
    @ApiOperation(value = "根据taskId、appType 拿到对应的job集合")
    public List<ScheduleJob> listJobsByTaskIdsAndApptype(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("appType") Integer appType) {
        return scheduleJobService.listJobsByTaskIdsAndApptype(taskIds, appType);
    }

    @RequestMapping(value = "/stopJobByJobId", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务ID 停止任务")
    public void stopJobByJobId(@RequestParam("jobId") String jobId, @RequestParam("appType") Integer appType) throws Exception {
        scheduleJobService.stopJobByJobId(jobId, appType);
    }

    @RequestMapping(value = "/buildTaskJobGraphTest", method = {RequestMethod.POST})
    @ApiOperation(value = "生成指定日期的周期实例(需要数据库无对应记录)")
    public void buildTaskJobGraphTest(@RequestParam("triggerDay") String triggerDay) {
        scheduleJobService.buildTaskJobGraphTest(triggerDay);
    }

    @RequestMapping(value = "/testTrigger", method = {RequestMethod.POST})
    public void testTrigger(@RequestParam("jobId") String jobId) {
        scheduleJobService.testTrigger(jobId);
    }

    @RequestMapping(value = "/getJobGraphJSON", method = {RequestMethod.POST, RequestMethod.GET})
    public String getJobGraphJSON(@RequestParam("jobId") String jobId) {
        return scheduleJobService.getJobGraphJSON(jobId);
    }

    @RequestMapping(value = "/updateNotRuleResult", method = {RequestMethod.POST})
    public void updateNotRuleResult(@RequestParam("jobId") String jobId, @RequestParam("rule") Integer rule, @RequestParam("resultLog") String result) {
        scheduleJobService.updateNotRuleResult(jobId, rule, result);
    }

    @RequestMapping(value = "/findTaskRuleJobById", method = {RequestMethod.POST})
    public List<ScheduleJobBeanVO> findTaskRuleJobById(@RequestParam("id") Long id) {
        return scheduleJobService.findTaskRuleJobById(id);
    }

    @RequestMapping(value = "/findTaskRuleJob", method = {RequestMethod.POST})
    public ScheduleDetailsVO findTaskRuleJob(@RequestParam("jobId") String jobId) {
        return scheduleJobService.findTaskRuleJob(jobId);
    }

    @RequestMapping(value = "/syncRestartJob", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean syncRestartJob(@RequestParam("id") Long id, @RequestParam("justRunChild") Boolean justRunChild, @RequestParam("setSuccess") Boolean setSuccess, @RequestParam("subJobIds") List<Long> subJobIds) {
        return scheduleJobService.syncRestartJob(id, justRunChild, setSuccess, subJobIds);
    }

    @RequestMapping(value = "/restartJobAndResume", method = {RequestMethod.POST, RequestMethod.GET})
    public OperatorVO restartJobAndResume(@RequestParam("jobIdList") List<Long> jobIdList, @RequestParam("runCurrentJob") Boolean runCurrentJob) {
        return scheduleJobService.restartJobAndResume(jobIdList, runCurrentJob);
    }

    @RequestMapping(value="/stopJobByCondition", method = {RequestMethod.POST})
    public Integer stopJobByCondition(@RequestBody ScheduleJobKillJobVO scheduleJobKillJobVO) {
        return scheduleJobService.stopJobByCondition(scheduleJobKillJobVO);
    }

    @RequestMapping(value = "getJobsRuleTime",method = {RequestMethod.POST})
    @ApiOperation(value = "根据规则转换时间")
    public List<ScheduleJobRuleTimeVO> getJobsRuleTime(@RequestBody List<ScheduleJobRuleTimeVO> jobList){
        return scheduleJobService.getJobsRuleTime(jobList);
    }

}
