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
    public ScheduleJob getJobById( long jobId);


    /**
     * 获取运
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    public PageResult getStatusJobList( Long projectId,  Long tenantId,  Integer appType,
                                        Long dtuicTenantId,  Integer status,  int pageSize,  int pageIndex);

    /**
     * 获取各个状态任务的数量
     */
    public Map<String, Object> getStatusCount( Long projectId,  Long tenantId,  Integer appType, Long dtuicTenantId);

    /**
     * 运行时长top排序
     */
    public List<JobTopOrderVO> runTimeTopOrder( Long projectId,
                                                Long startTime,
                                                Long endTime,  Integer appType, Long dtuicTenantId);

    /**
     * 近30天任务出错排行
     */
    public List<JobTopErrorVO> errorTopOrder( Long projectId,  Long tenantId,  Integer appType, Long dtuicTenantId);


    /**
     * 曲线图数据
     */
    public ScheduleJobChartVO getJobGraph( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId);

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    public ChartDataVO getScienceJobGraph( long projectId,  Long tenantId,
                                           String taskType);

    public Map<String, Object> countScienceJobStatus( List<Long> projectIds,  Long tenantId,  Integer runStatus,  Integer type,  String taskType,
                                                      String cycStartTime,  String cycEndTime);

    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    public PageResult<List<ScheduleJobVO>> queryJobs(QueryJobDTO vo) throws Exception;

    public List<SchedulePeriodInfoVO> displayPeriods( boolean isAfter,  Long jobId,  Long projectId,  int limit) throws Exception;

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleJobVO getRelatedJobs( String jobId,  String query) throws Exception;

    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    public Map<String, Long> queryJobsStatusStatistics(QueryJobDTO vo);


    public List<ScheduleRunDetailVO> jobDetail( Long taskId,  Integer appType);


    /**
     * 触发 engine 执行指定task
     */
    public void sendTaskStartTrigger(ScheduleJob scheduleJob) throws Exception;

    public String stopJob( long jobId,  Long userId,  Long projectId,  Long tenantId,  Long dtuicTenantId,
                           Boolean isRoot,  Integer appType) throws Exception;


    public void stopFillDataJobs( String fillDataJobName,  Long projectId,  Long dtuicTenantId,  Integer appType) throws Exception;


    public int batchStopJobs( List<Long> jobIdList,
                              Long projectId,
                              Long dtuicTenantId,
                              Integer appType);


    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    public String fillTaskData( String taskJsonStr,  String fillName,
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
    public PageResult<ScheduleFillDataJobPreViewVO> getFillDataJobInfoPreview( String jobName,  Long runDay,
                                                                               Long bizStartDay,  Long bizEndDay,  Long dutyUserId,
                                                                               Long projectId,  Integer appType,  Integer userId,
                                                                               Integer currentPage,  Integer pageSize,  Long tenantId);

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                             String fillJobName,
                                                                             Long dutyUserId) throws Exception;

    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo( String queryJobDTO,
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
    public ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData( String jobId,  String query,
                                                                                 String fillJobName) throws Exception;


    /**
     * 获取重跑的数据节点信息
     */
    public List<RestartJobVO> getRestartChildJob( String jobKey,  Long parentTaskId,  boolean isOnlyNextChild);


    public List<String> listJobIdByTaskNameAndStatusList( String taskName,  List<Integer> statusList,  Long projectId, Integer appType);


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    public Map<String, ScheduleJob> getLabTaskRelationMap( List<String> jobIdList,  Long projectId);

    /**
     * 获取任务执行信息
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    public List<Map<String, Object>> statisticsTaskRecentInfo( Long taskId,  Integer appType,  Long projectId,  Integer count);


    /**
     * 批量更新
     *
     * @param jobs
     */
    public Integer BatchJobsBatchUpdate( String jobs);

    /**
     *  把开始时间和结束时间置为null
     * @param jobId
     * @return
     */
    public Integer updateTimeNull( String jobId);


    public ScheduleJob getById( Long id);

    public ScheduleJob getByJobId( String jobId,  Integer isDeleted);

    public List<ScheduleJob> getByIds( List<Long> ids,  Long projectId);


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @param appType
     * @return
     */
    public List<ScheduleJob> getSameDayChildJob( String batchJob,
                                                 boolean isOnlyNextChild,  Integer appType);

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param scheduleJob
     * @return
     */
    public List<ScheduleJob> getAllChildJobWithSameDay(ScheduleJob scheduleJob,
                                                        boolean isOnlyNextChild,  Integer appType);


    public Integer generalCount(ScheduleJobDTO query);

    public Integer generalCountWithMinAndHour(ScheduleJobDTO query);


    public List<ScheduleJob> generalQuery(PageQuery query);

    public List<ScheduleJob> generalQueryWithMinAndHour(PageQuery query);

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    public ScheduleJob getLastSuccessJob( Long taskId,  Timestamp time, Integer appType);


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
    public ScheduleServerLogVO setAlogrithmLabLog( Integer status,  Integer taskType,  String jobId,
                                                   String info,  String logVo,  Integer appType) throws Exception;



    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     */
    public List<ScheduleJobVO> minOrHourJobQuery(ScheduleJobDTO scheduleJobDTO);


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    public void updateJobStatusAndLogInfo( String jobId,  Integer status,  String logInfo);


    /**
     * 测试任务 是否可以运行
     * @param jobId
     * @return
     */
    public String testCheckCanRun(String jobId);

    /**
     * 生成当天任务实例
     * @throws Exception
     */
    public void createTodayTaskShade( Long taskId, Integer appType);

    public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query);

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    public List<ScheduleJob> listByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType);

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    public List<ScheduleJob> listByCyctimeAndJobName( Long startId,  String preCycTime,  String preJobName,  Integer scheduleType,  Integer batchJobSize);

    public Integer countByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType);

    /**
     * 根据jobKey删除job jobjob记录
     * @param jobKeyList
     */
    public void deleteJobsByJobKey( List<String> jobKeyList);


    public List<ScheduleJob> syncBatchJob(QueryJobDTO dto);

    /**
     *
     * 根据taskId、appType 拿到对应的job集合
     * @param taskIds
     * @param appType
     */
    public List<ScheduleJob> listJobsByTaskIdsAndApptype( List<Long> taskIds, Integer appType);

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
    String stopJobByJobId( String jobId,  Long userId,  Long projectId,  Long tenantId,  Long dtuicTenantId,
                           Boolean isRoot,  Integer appType) throws Exception;

}
