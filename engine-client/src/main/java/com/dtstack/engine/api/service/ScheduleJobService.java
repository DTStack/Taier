package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import com.dtstack.engine.api.vo.ScheduleServerLogVO;
import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.RestartJobVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
public interface ScheduleJobService extends DtInsightServer {

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/getJobById")
    ApiResponse<ScheduleJob> getJobById(@Param("jobId") long jobId);


    /**
     * 获取运
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getStatusJobList")
    ApiResponse<PageResult> getStatusJobList(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,
                                             @Param("dtuicTenantId") Long dtuicTenantId, @Param("status") Integer status, @Param("pageSize") int pageSize, @Param("pageIndex") int pageIndex);

    /**
     * 获取各个状态任务的数量
     */
    @RequestLine("POST /node/scheduleJob/getStatusCount")
    ApiResponse<ScheduleJobStatusVO> getStatusCount(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 运行时长top排序
     */
    @RequestLine("POST /node/scheduleJob/runTimeTopOrder")
    ApiResponse<List<JobTopOrderVO>> runTimeTopOrder(@Param("projectId") Long projectId,
                                                     @Param("startTime") Long startTime,
                                                     @Param("endTime") Long endTime, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 近30天任务出错排行
     */
    @RequestLine("POST /node/scheduleJob/errorTopOrder")
    ApiResponse<List<JobTopErrorVO>> errorTopOrder(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);


    /**
     * 曲线图数据
     */
    @RequestLine("POST /node/scheduleJob/getJobGraph")
    ApiResponse<ScheduleJobChartVO> getJobGraph(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getScienceJobGraph")
    ApiResponse<ChartDataVO> getScienceJobGraph(@Param("projectId") long projectId, @Param("tenantId") Long tenantId,
                                                @Param("taskType") String taskType);

    @RequestLine("POST /node/scheduleJob/countScienceJobStatus")
    ApiResponse<ScheduleJobScienceJobStatusVO> countScienceJobStatus(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId, @Param("runStatus") Integer runStatus, @Param("type") Integer type, @Param("taskType") String taskType,
                                                                     @Param("cycStartDay") String cycStartTime, @Param("cycEndDay") String cycEndTime);

    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/queryJobs")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<PageResult<List<ScheduleJobVO>>> queryJobs( QueryJobDTO vo) ;

    @RequestLine("POST /node/scheduleJob/displayPeriods")
    ApiResponse<List<SchedulePeriodInfoVO>> displayPeriods(@Param("isAfter") boolean isAfter, @Param("jobId") Long jobId, @Param("projectId") Long projectId, @Param("limit") int limit) ;

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @
     */
    @RequestLine("POST /node/scheduleJob/getRelatedJobs")
    ApiResponse<ScheduleJobVO> getRelatedJobs(@Param("jobId") String jobId, @Param("vo") String query) ;

    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/queryJobsStatusStatistics")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Map<String, Long>> queryJobsStatusStatistics( QueryJobDTO vo);


    @RequestLine("POST /node/scheduleJob/jobDetail")
    ApiResponse<List<ScheduleRunDetailVO>> jobDetail(@Param("taskId") Long taskId, @Param("appType") Integer appType);


    /**
     * 触发 engine 执行指定task
     */
    @RequestLine("POST /node/scheduleJob/sendTaskStartTrigger")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> sendTaskStartTrigger( ScheduleJob scheduleJob) ;

    @RequestLine("POST /node/scheduleJob/stopJob")
    ApiResponse<String> stopJob(@Param("jobId") long jobId, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                                @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType) ;


    @RequestLine("POST /node/scheduleJob/stopFillDataJobs")
    ApiResponse<Void> stopFillDataJobs(@Param("fillDataJobName") String fillDataJobName, @Param("projectId") Long projectId, @Param("dtuicTenantId") Long dtuicTenantId, @Param("appType") Integer appType) ;


    @RequestLine("POST /node/scheduleJob/batchStopJobs")
    ApiResponse<Integer> batchStopJobs(@Param("jobIdList") List<Long> jobIdList,
                                       @Param("projectId") Long projectId,
                                       @Param("dtuicTenantId") Long dtuicTenantId,
                                       @Param("appType") Integer appType);


    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    @RequestLine("POST /node/scheduleJob/fillTaskData")
    ApiResponse<String> fillTaskData(@Param("taskJson") String taskJsonStr, @Param("fillName") String fillName,
                                     @Param("fromDay") Long fromDay, @Param("toDay") Long toDay,
                                     @Param("concreteStartTime") String beginTime, @Param("concreteEndTime") String endTime,
                                     @Param("projectId") Long projectId, @Param("userId") Long userId,
                                     @Param("tenantId") Long tenantId,
                                     @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId) ;


    /**
     * 先查询出所有的补数据名称
     * <p>
     * jobName dutyUserId userId 需要关联task表（防止sql慢） 其他情况不需要
     *
     * @param jobName
     * @param runDay
     * @param bizStartDay
     * @param bizEndDay
     * @param dutyUserId
     * @param projectId
     * @param appType
     * @param userId
     * @param currentPage
     * @param pageSize
     * @param tenantId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getFillDataJobInfoPreview")
    ApiResponse<PageResult<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(@Param("jobName") String jobName, @Param("runDay") Long runDay,
                                                                                    @Param("bizStartDay") Long bizStartDay, @Param("bizEndDay") Long bizEndDay, @Param("dutyUserId") Long dutyUserId,
                                                                                    @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("user") Integer userId,
                                                                                    @Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("tenantId") Long tenantId);

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    @RequestLine("POST /node/scheduleJob/getFillDataDetailInfoOld?fillJobName={fillJobName}&dutyUserId={dutyUserId}")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<PageResult<ScheduleFillDataJobDetailVO>> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                                  @Param("fillJobName") String fillJobName,
                                                                                  @Param("dutyUserId") Long dutyUserId);

    @RequestLine("POST /node/scheduleJob/getFillDataDetailInfo")
    ApiResponse<PageResult<ScheduleFillDataJobDetailVO>> getFillDataDetailInfo(@Param("vo") String queryJobDTO,
                                                                               @Param("flowJobIdList") List<String> flowJobIdList,
                                                                               @Param("fillJobName") String fillJobName,
                                                                               @Param("dutyUserId") Long dutyUserId, @Param("searchType") String searchType,@Param("appType") Integer appType) ;

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @
     */
    @RequestLine("POST /node/scheduleJob/getRelatedJobsForFillData")
    ApiResponse<ScheduleFillDataJobDetailVO.FillDataRecord> getRelatedJobsForFillData(@Param("jobId") String jobId, @Param("vo") String query,
                                                                                      @Param("fillJobName") String fillJobName) ;


    /**
     * 获取重跑的数据节点信息
     */
    @RequestLine("POST /node/scheduleJob/getRestartChildJob")
    ApiResponse<List<RestartJobVO>> getRestartChildJob(@Param("jobKey") String jobKey, @Param("taskId") Long parentTaskId, @Param("isOnlyNextChild") boolean isOnlyNextChild);


    @RequestLine("POST /node/scheduleJob/listJobIdByTaskNameAndStatusList")
    ApiResponse<List<String>> listJobIdByTaskNameAndStatusList(@Param("taskName") String taskName, @Param("statusList") List<Integer> statusList, @Param("projectId") Long projectId, @Param("appType") Integer appType);


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getLabTaskRelationMap")
    ApiResponse<Map<String, ScheduleJob>> getLabTaskRelationMap(@Param("jobIdList") List<String> jobIdList, @Param("projectId") Long projectId);

    /**
     * 获取任务执行信息
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    @RequestLine("POST /node/scheduleJob/statisticsTaskRecentInfo")
    ApiResponse<List<Map<String, Object>>> statisticsTaskRecentInfo(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId, @Param("count") Integer count);


    /**
     * 批量更新
     *
     * @param jobs
     */
    @RequestLine("POST /node/scheduleJob/BatchJobsBatchUpdate")
    ApiResponse<Integer> BatchJobsBatchUpdate(@Param("jobs") String jobs);

    /**
     *  把开始时间和结束时间置为null
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/updateTimeNull")
    ApiResponse<Integer> updateTimeNull(@Param("jobId") String jobId);


    @RequestLine("POST /node/scheduleJob/getById")
    ApiResponse<ScheduleJob> getById(@Param("id") Long id);

    @RequestLine("POST /node/scheduleJob/getByJobId")
    ApiResponse<ScheduleJob> getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    @RequestLine("POST /node/scheduleJob/getByIds")
    ApiResponse<List<ScheduleJob>> getByIds(@Param("ids") List<Long> ids, @Param("project") Long projectId);


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getSameDayChildJob")
    ApiResponse<List<ScheduleJob>> getSameDayChildJob(@Param("batchJob") String batchJob,
                                                      @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType);

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param scheduleJob
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getAllChildJobWithSameDay?isOnlyNextChild={isOnlyNextChild}&appType={appType}")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> getAllChildJobWithSameDay(ScheduleJob scheduleJob,
                                                             @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType);


    @RequestLine("POST /node/scheduleJob/generalCount")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Integer> generalCount( ScheduleJobDTO query);

    @RequestLine("POST /node/scheduleJob/generalCountWithMinAndHour")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Integer> generalCountWithMinAndHour(ScheduleJobDTO query);


    @RequestLine("POST /node/scheduleJob/generalQuery")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> generalQuery(PageQuery query);

    @RequestLine("POST /node/scheduleJob/generalQueryWithMinAndHour")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> generalQueryWithMinAndHour( PageQuery query);

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getLastSuccessJob")
    ApiResponse<ScheduleJob> getLastSuccessJob(@Param("taskId") Long taskId, @Param("time") Timestamp time, @Param("appType") Integer appType);


    /**
     * 设置算法实验日志
     * 获取全部子节点日志
     *
     * @param status
     * @param taskType
     * @param jobId
     * @param logVo
     * @
     */
    @RequestLine("POST /node/scheduleJob/setAlogrithmLabLog")
    ApiResponse<ScheduleServerLogVO> setAlogrithmLabLog(@Param("status") Integer status, @Param("taskType") Integer taskType, @Param("jobId") String jobId,
                                                        @Param("info") String info, @Param("logVo") String logVo, @Param("appType") Integer appType) ;



    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     */
    @RequestLine("POST /node/scheduleJob/minOrHourJobQuery")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<ScheduleJobVO>> minOrHourJobQuery(ScheduleJobDTO scheduleJobDTO);


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    @RequestLine("POST /node/scheduleJob/updateJobStatusAndLogInfo")
    ApiResponse<Void> updateJobStatusAndLogInfo(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo);


    /**
     * 测试任务 是否可以运行
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/testCheckCanRun")
    ApiResponse<String> testCheckCanRun(@Param("jobId") String jobId);

    /**
     * 生成当天任务实例
     * @
     */
    @RequestLine("POST /node/scheduleJob/createTodayTaskShade")
    ApiResponse<Void> createTodayTaskShade(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleJob/listByBusinessDateAndPeriodTypeAndStatusList")
    ApiResponse<List<ScheduleJob>> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query);

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/listByCyctimeAndJobName")
    ApiResponse<List<ScheduleJob>> listByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    @RequestLine("POST /node/scheduleJob/listByCyctimeAndJobNameWithStartId")
    ApiResponse<List<ScheduleJob>> listByCyctimeAndJobName(@Param("startId") Long startId, @Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType, @Param("batchJobSize") Integer batchJobSize);

    @RequestLine("POST /node/scheduleJob/countByCyctimeAndJobName")
    ApiResponse<Integer> countByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    /**
     * 根据jobKey删除job jobjob记录
     * @param jobKeyList
     */
    @RequestLine("POST /node/scheduleJob/deleteJobsByJobKey")
    ApiResponse<Void> deleteJobsByJobKey(@Param("jobKeyList") List<String> jobKeyList);


    @RequestLine("POST /node/scheduleJob/syncBatchJob")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<List<ScheduleJob>> syncBatchJob( QueryJobDTO dto);

    /**
     *
     * 根据taskId、appType 拿到对应的job集合
     * @param taskIds
     * @param appType
     */
    @RequestLine("POST /node/scheduleJob/listJobsByTaskIdsAndApptype")
    ApiResponse<List<ScheduleJob>> listJobsByTaskIdsAndApptype(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType);

    /**
     * 根据任务ID 停止任务
     *
     * @param jobId
     * @param userId
     * @param projectId
     * @param tenantId
     * @param dtuicTenantId
     * @param isRoot
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/stopJobByJobId")
    ApiResponse<String> stopJobByJobId(@Param("jobId") String jobId, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                                       @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType) ;

    /**
     * 生成指定日期的周期实例(需要数据库无对应记录)
     *
     * @param triggerDay
     * @return
     */
    @RequestLine("POST /node/scheduleJob/buildTaskJobGraphTest")
    ApiResponse<Void> buildTaskJobGraphTest(@Param("triggerDay") String triggerDay);

    @RequestLine("POST /node/scheduleJob/testTrigger")
    ApiResponse<Void> testTrigger(@Param("jobId") String jobId);

}
