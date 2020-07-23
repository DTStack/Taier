package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
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
import com.dtstack.sdk.core.common.DtInsightServer;
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
    ScheduleJob getJobById( long jobId);


    /**
     * 获取运
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getStatusJobList")
    PageResult getStatusJobList( Long projectId,  Long tenantId,  Integer appType,
                                 Long dtuicTenantId,  Integer status,  int pageSize,  int pageIndex);

    /**
     * 获取各个状态任务的数量
     */
    @RequestLine("POST /node/scheduleJob/getStatusCount")
    Map<String, Object> getStatusCount( Long projectId,  Long tenantId,  Integer appType, Long dtuicTenantId);

    /**
     * 运行时长top排序
     */
    @RequestLine("POST /node/scheduleJob/runTimeTopOrder")
    List<JobTopOrderVO> runTimeTopOrder( Long projectId,
                                         Long startTime,
                                         Long endTime,  Integer appType, Long dtuicTenantId);

    /**
     * 近30天任务出错排行
     */
    @RequestLine("POST /node/scheduleJob/errorTopOrder")
    List<JobTopErrorVO> errorTopOrder( Long projectId,  Long tenantId,  Integer appType, Long dtuicTenantId);


    /**
     * 曲线图数据
     */
    @RequestLine("POST /node/scheduleJob/getJobGraph")
    ScheduleJobChartVO getJobGraph( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId);

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getScienceJobGraph")
    ChartDataVO getScienceJobGraph( long projectId,  Long tenantId,
                                    String taskType);

    @RequestLine("POST /node/scheduleJob/countScienceJobStatus")
    Map<String, Object> countScienceJobStatus( List<Long> projectIds,  Long tenantId,  Integer runStatus,  Integer type,  String taskType,
                                               String cycStartTime,  String cycEndTime);

    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/queryJobs")
    PageResult<List<ScheduleJobVO>> queryJobs(QueryJobDTO vo) throws Exception;

    @RequestLine("POST /node/scheduleJob/displayPeriods")
    List<SchedulePeriodInfoVO> displayPeriods( boolean isAfter,  Long jobId,  Long projectId,  int limit) throws Exception;

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @RequestLine("POST /node/scheduleJob/getRelatedJobs")
    ScheduleJobVO getRelatedJobs( String jobId,  String query) throws Exception;

    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJob/queryJobsStatusStatistics")
    Map<String, Long> queryJobsStatusStatistics(QueryJobDTO vo);


    @RequestLine("POST /node/scheduleJob/jobDetail")
    List<ScheduleRunDetailVO> jobDetail( Long taskId,  Integer appType);


    /**
     * 触发 engine 执行指定task
     */
    @RequestLine("POST /node/scheduleJob/sendTaskStartTrigger")
    void sendTaskStartTrigger(ScheduleJob scheduleJob) throws Exception;

    @RequestLine("POST /node/scheduleJob/stopJob")
    String stopJob( long jobId,  Long userId,  Long projectId,  Long tenantId,  Long dtuicTenantId,
                    Boolean isRoot,  Integer appType) throws Exception;


    @RequestLine("POST /node/scheduleJob/stopFillDataJobs")
    void stopFillDataJobs( String fillDataJobName,  Long projectId,  Long dtuicTenantId,  Integer appType) throws Exception;


    @RequestLine("POST /node/scheduleJob/batchStopJobs")
    int batchStopJobs( List<Long> jobIdList,
                       Long projectId,
                       Long dtuicTenantId,
                       Integer appType);


    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    @RequestLine("POST /node/scheduleJob/fillTaskData")
    String fillTaskData( String taskJsonStr,  String fillName,
                         Long fromDay,  Long toDay,
                         String beginTime,  String endTime,
                         Long projectId,  Long userId,
                         Long tenantId,
                         Boolean isRoot,  Integer appType,  Long dtuicTenantId) throws Exception;


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
    PageResult<ScheduleFillDataJobPreViewVO> getFillDataJobInfoPreview( String jobName,  Long runDay,
                                                                        Long bizStartDay,  Long bizEndDay,  Long dutyUserId,
                                                                        Long projectId,  Integer appType,  Integer userId,
                                                                        Integer currentPage,  Integer pageSize,  Long tenantId);

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    @RequestLine("POST /node/scheduleJob/getFillDataDetailInfoOld")
    PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                     String fillJobName,
                                                                     Long dutyUserId) throws Exception;

    @RequestLine("POST /node/scheduleJob/getFillDataDetailInfo")
    PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo( String queryJobDTO,
                                                                   List<String> flowJobIdList,
                                                                   String fillJobName,
                                                                   Long dutyUserId,  String searchType) throws Exception;

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @RequestLine("POST /node/scheduleJob/getRelatedJobsForFillData")
    ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData( String jobId,  String query,
                                                                          String fillJobName) throws Exception;


    /**
     * 获取重跑的数据节点信息
     */
    @RequestLine("POST /node/scheduleJob/getRestartChildJob")
    List<RestartJobVO> getRestartChildJob( String jobKey,  Long parentTaskId,  boolean isOnlyNextChild);


    @RequestLine("POST /node/scheduleJob/listJobIdByTaskNameAndStatusList")
    List<String> listJobIdByTaskNameAndStatusList( String taskName,  List<Integer> statusList,  Long projectId, Integer appType);


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getLabTaskRelationMap")
    Map<String, ScheduleJob> getLabTaskRelationMap( List<String> jobIdList,  Long projectId);

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
    List<Map<String, Object>> statisticsTaskRecentInfo( Long taskId,  Integer appType,  Long projectId,  Integer count);


    /**
     * 批量更新
     *
     * @param jobs
     */
    @RequestLine("POST /node/scheduleJob/BatchJobsBatchUpdate")
    Integer BatchJobsBatchUpdate( String jobs);

    /**
     *  把开始时间和结束时间置为null
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/updateTimeNull")
    Integer updateTimeNull( String jobId);


    @RequestLine("POST /node/scheduleJob/getById")
    ScheduleJob getById( Long id);

    @RequestLine("POST /node/scheduleJob/getByJobId")
    ScheduleJob getByJobId( String jobId,  Integer isDeleted);

    @RequestLine("POST /node/scheduleJob/getByIds")
    List<ScheduleJob> getByIds( List<Long> ids,  Long projectId);


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getSameDayChildJob")
    List<ScheduleJob> getSameDayChildJob( String batchJob,
                                          boolean isOnlyNextChild,  Integer appType);

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param scheduleJob
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getAllChildJobWithSameDay")
    List<ScheduleJob> getAllChildJobWithSameDay(ScheduleJob scheduleJob,
                                                boolean isOnlyNextChild,  Integer appType);


    @RequestLine("POST /node/scheduleJob/generalCount")
    Integer generalCount(ScheduleJobDTO query);

    @RequestLine("POST /node/scheduleJob/generalCountWithMinAndHour")
    Integer generalCountWithMinAndHour(ScheduleJobDTO query);


    @RequestLine("POST /node/scheduleJob/generalQuery")
    List<ScheduleJob> generalQuery(PageQuery query);

    @RequestLine("POST /node/scheduleJob/generalQueryWithMinAndHour")
    List<ScheduleJob> generalQueryWithMinAndHour(PageQuery query);

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    @RequestLine("POST /node/scheduleJob/getLastSuccessJob")
    ScheduleJob getLastSuccessJob( Long taskId,  Timestamp time, Integer appType);


    /**
     * 设置算法实验日志
     * 获取全部子节点日志
     *
     * @param status
     * @param taskType
     * @param jobId
     * @param logVo
     * @throws Exception
     */
    @RequestLine("POST /node/scheduleJob/setAlogrithmLabLog")
    ScheduleServerLogVO setAlogrithmLabLog( Integer status,  Integer taskType,  String jobId,
                                            String info,  String logVo,  Integer appType) throws Exception;



    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     */
    @RequestLine("POST /node/scheduleJob/minOrHourJobQuery")
    List<ScheduleJobVO> minOrHourJobQuery(ScheduleJobDTO scheduleJobDTO);


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    @RequestLine("POST /node/scheduleJob/updateJobStatusAndLogInfo")
    void updateJobStatusAndLogInfo( String jobId,  Integer status,  String logInfo);


    /**
     * 测试任务 是否可以运行
     * @param jobId
     * @return
     */
    @RequestLine("POST /node/scheduleJob/testCheckCanRun")
    String testCheckCanRun(String jobId);

    /**
     * 生成当天任务实例
     * @throws Exception
     */
    @RequestLine("POST /node/scheduleJob/createTodayTaskShade")
    void createTodayTaskShade( Long taskId, Integer appType);

    @RequestLine("POST /node/scheduleJob/listByBusinessDateAndPeriodTypeAndStatusList")
    List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query);

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    @RequestLine("POST /node/scheduleJob/listByCyctimeAndJobName")
    List<ScheduleJob> listByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType);

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    @RequestLine("POST /node/scheduleJob/listByCyctimeAndJobName")
    List<ScheduleJob> listByCyctimeAndJobName( Long startId,  String preCycTime,  String preJobName,  Integer scheduleType,  Integer batchJobSize);

    @RequestLine("POST /node/scheduleJob/countByCyctimeAndJobName")
    Integer countByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType);

    /**
     * 根据jobKey删除job jobjob记录
     * @param jobKeyList
     */
    @RequestLine("POST /node/scheduleJob/deleteJobsByJobKey")
    void deleteJobsByJobKey( List<String> jobKeyList);


    @RequestLine("POST /node/scheduleJob/syncBatchJob")
    List<ScheduleJob> syncBatchJob(QueryJobDTO dto);

    /**
     *
     * 根据taskId、appType 拿到对应的job集合
     * @param taskIds
     * @param appType
     */
    @RequestLine("POST /node/scheduleJob/listJobsByTaskIdsAndApptype")
    List<ScheduleJob> listJobsByTaskIdsAndApptype( List<Long> taskIds, Integer appType);

    /**
     * 根据任务ID 停止任务
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
    String stopJobByJobId( String jobId,  Long userId,  Long projectId,  Long tenantId,  Long dtuicTenantId,
                           Boolean isRoot,  Integer appType) throws Exception;

}
