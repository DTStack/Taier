package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.BatchJob;
import com.dtstack.engine.api.dto.BatchJobDTO;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.BatchFillDataJobDetailVO;
import com.dtstack.engine.api.vo.BatchFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.BatchJobChartVO;
import com.dtstack.engine.api.vo.BatchJobVO;
import com.dtstack.engine.api.vo.BatchPeriodInfoVO;
import com.dtstack.engine.api.vo.BatchRunDetailVO;
import com.dtstack.engine.api.vo.BatchServerLogVO;
import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.RestartJobVO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
public interface ScheduleJobService {

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    public BatchJob getJobById(@Param("jobId") long jobId);


    /**
     * 获取运
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    public PageResult getStatusJobList(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,
                                       @Param("dtuicTenantId") Long dtuicTenantId, @Param("status") Integer status, @Param("pageSize") int pageSize, @Param("pageIndex") int pageIndex);

    /**
     * 获取各个状态任务的数量
     */
    public Map<String, Object> getStatusCount(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 运行时长top排序
     */
    public List<JobTopOrderVO> runTimeTopOrder(@Param("projectId") Long projectId,
                                               @Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 近30天任务出错排行
     */
    public List<JobTopErrorVO> errorTopOrder(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId);


    /**
     * 曲线图数据
     */
    public BatchJobChartVO getJobGraph(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType,@Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    public ChartDataVO getScienceJobGraph(@Param("projectId") long projectId, @Param("tenantId") Long tenantId,
                                          @Param("taskType") Integer taskType);

    public Map<String, Object> countScienceJobStatus(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId, @Param("runStatus") Integer runStatus, @Param("type") Integer type, @Param("taskType") Integer taskType);

    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    public PageResult<List<BatchJobVO>> queryJobs(QueryJobDTO vo) throws Exception;

    public List<BatchPeriodInfoVO> displayPeriods(@Param("isAfter") boolean isAfter, @Param("jobId") Long jobId, @Param("projectId") Long projectId, @Param("limit") int limit) throws Exception;

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public BatchJobVO getRelatedJobs(@Param("jobId") String jobId, @Param("vo") String query) throws Exception;

    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    public Map<String, Long> queryJobsStatusStatistics(QueryJobDTO vo);


    public List<BatchRunDetailVO> jobDetail(@Param("taskId") Long taskId, @Param("appType") Integer appType);


    /**
     * 触发 engine 执行指定task
     */
    public void sendTaskStartTrigger(BatchJob batchJob) throws Exception;

    public String stopJob(@Param("jobId") long jobId, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("dtuicTenantId") Long dtuicTenantId,
                          @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType) throws Exception;


    public void stopFillDataJobs(@Param("fillDataJobName") String fillDataJobName, @Param("projectId") Long projectId, @Param("dtuicTenantId") Long dtuicTenantId, @Param("appType") Integer appType) throws Exception;


    public int batchStopJobs(@Param("jobIdList") List<Long> jobIdList,
                             @Param("projectId") Long projectId,
                             @Param("dtuicTenantId") Long dtuicTenantId,
                             @Param("appType") Integer appType);


    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    public String fillTaskData(@Param("taskJson") String taskJsonStr, @Param("fillName") String fillName,
                               @Param("fromDay") Long fromDay, @Param("toDay") Long toDay,
                               @Param("concreteStartTime") String beginTime, @Param("concreteEndTime") String endTime,
                               @Param("projectId") Long projectId, @Param("userId") Long userId,
                               @Param("tenantId") Long tenantId,
                               @Param("isRoot") Boolean isRoot, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId) throws Exception;


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
    public PageResult<BatchFillDataJobPreViewVO> getFillDataJobInfoPreview(@Param("jobName") String jobName, @Param("runDay") Long runDay,
                                                                           @Param("bizStartDay") Long bizStartDay, @Param("bizEndDay") Long bizEndDay, @Param("dutyUserId") Long dutyUserId,
                                                                           @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("user") Integer userId,
                                                                           @Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("tenantId") Long tenantId);

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    public PageResult<BatchFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                         @Param("fillJobName") String fillJobName,
                                                                         @Param("dutyUserId") Long dutyUserId) throws Exception;

    public PageResult<BatchFillDataJobDetailVO> getFillDataDetailInfo(@Param("vo") String queryJobDTO,
                                                                      @Param("flowJobIdList") List<String> flowJobIdList,
                                                                      @Param("fillJobName") String fillJobName,
                                                                      @Param("dutyUserId") Long dutyUserId, @Param("searchType") String searchType) throws Exception;

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public BatchFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData(@Param("jobId") String jobId, @Param("vo") String query,
                                                                             @Param("fillJobName") String fillJobName) throws Exception;


    /**
     * 获取重跑的数据节点信息
     */
    public List<RestartJobVO> getRestartChildJob(@Param("jobKey") String jobKey, @Param("taskId") Long parentTaskId, @Param("isOnlyNextChild") boolean isOnlyNextChild);


    public List<String> listJobIdByTaskNameAndStatusList(@Param("taskName") String taskName, @Param("statusList") List<Integer> statusList, @Param("projectId") Long projectId,@Param("appType") Integer appType);


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    public Map<String, BatchJob> getLabTaskRelationMap(@Param("jobIdList") List<String> jobIdList, @Param("projectId") Long projectId);

    /**
     * 获取任务执行信息
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    public List<Map<String, Object>> statisticsTaskRecentInfo(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId, @Param("count") Integer count);


    /**
     * 批量更新
     *
     * @param jobs
     */
    public Integer BatchJobsBatchUpdate(@Param("jobs") String jobs);

    /**
     *  把开始时间和结束时间置为null
     * @param jobId
     * @return
     */
    public Integer updateTimeNull(@Param("jobId") String jobId);


    public BatchJob getById(@Param("id") Long id);

    public BatchJob getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    public List<BatchJob> getByIds(@Param("ids") List<Long> ids, @Param("project") Long projectId);


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    public List<BatchJob> getSameDayChildJob(@Param("batchJob") String batchJob,
                                             @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType);

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param batchJob
     * @return
     */
    public List<BatchJob> getAllChildJobWithSameDay(BatchJob batchJob,
                                                    @Param("isOnlyNextChild") boolean isOnlyNextChild, @Param("appType") Integer appType);


    public Integer generalCount(BatchJobDTO query);

    public Integer generalCountWithMinAndHour(BatchJobDTO query);


    public List<BatchJob> generalQuery(PageQuery query);

    public List<BatchJob> generalQueryWithMinAndHour(PageQuery query);

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    public BatchJob getLastSuccessJob(@Param("taskId") Long taskId, @Param("time") Timestamp time);


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
    public BatchServerLogVO setAlogrithmLabLog(@Param("status") Integer status, @Param("taskType") Integer taskType, @Param("jobId") String jobId,
                                               @Param("info") String info, @Param("logVo") String logVo, @Param("appType") Integer appType) throws Exception;



    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     */
    public List<BatchJobVO> minOrHourJobQuery(BatchJobDTO batchJobDTO);


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    public void updateJobStatusAndLogInfo(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo);


    /**
     * 测试任务 是否可以运行
     * @param jobId
     * @return
     */
    public String testCheckCanRun(@Param("jobId")String jobId);

    /**
     * 生成当天任务实例
     * @throws Exception
     */
    public void createTodayTaskShade(@Param("taskId") Long taskId,@Param("appType") Integer appType);

    public List<BatchJob> listByBusinessDateAndPeriodTypeAndStatusList(BatchJobDTO query);

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    public List<BatchJob> listByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    public List<BatchJob> listByCyctimeAndJobName(@Param("startId") Long startId, @Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType, @Param("batchJobSize") Integer batchJobSize);

    public Integer countByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    /**
     * 根据jobKey删除job jobjob记录
     * @param jobKeyList
     */
    public void deleteJobsByJobKey(@Param("jobKeyList") List<String> jobKeyList);
}
