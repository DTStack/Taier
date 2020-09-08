package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.po.SimpleScheduleJobPO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobDao {

    ScheduleJob getOne(@Param("id") Long id);

    List<ScheduleJob> listByJobIds(@Param("jobIds") List<Long> jobIds);

    ScheduleJob getByJobKeyAndType(@Param("jobKey") String jobKey, @Param("type") int type);

    ScheduleJob getByJobKey(@Param("jobKey") String jobKey);

    List<Map<String, Object>> countByStatusAndType(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status);

    List<Map<String, Object>> selectStatusAndType(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType,
                                                  @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status, @Param("startPage") Integer startPage, @Param("pageSize") Integer pageSize);

    List<Map<String, Object>> listTopRunTime(@Param("projectId") Long projectId, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listTopErrorByType(@Param("dtuicTenantId") Long dtuicTenantId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("type") Integer type, @Param("time") Timestamp time, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType);

    List<Map<String, Object>> listTodayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listYesterdayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listMonthJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listThirtyDayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("taskTypes") List<Integer> taskTypes, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    List<ScheduleJob> listRestartBatchJobList(@Param("type") int type, @Param("status") Integer taskStatus, @Param("lastTime") Timestamp lastTime);

    List<ScheduleJob> listJobByJobKeys(@Param("jobKeys") Collection<String> jobKeys);

    List<Long> listIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("appType") Integer appType);

    List<String> listJobIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("statuses") List<Integer> status);

    List<Map<String, String>> listTaskExeTimeInfo(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery);

    ScheduleJob getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    Long getId(@Param("jobId") String jobId);

    List<ScheduleJob> generalQuery(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> generalScienceQuery(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> generalQueryWithMinAndHour(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> listAfterOrBeforeJobs(@Param("taskId") Long taskId, @Param("isAfter") boolean isAfter, @Param("cycTime") String cycTime);

    Integer generalCount(@Param("model") ScheduleJobDTO object);

    Integer generalScienceCount(@Param("model") ScheduleJobDTO object);

    Integer generalCountWithMinAndHour(@Param("model") ScheduleJobDTO object);

    Integer minOrHourJobCount(@Param("model") ScheduleJobDTO object);

    List<ScheduleJob> minOrHourJobQuery(PageQuery<ScheduleJobDTO> pageQuery);


    List<Map<Integer, Long>> getJobsStatusStatistics(@Param("model") ScheduleJobDTO object);

    Integer batchInsert(Collection batchJobs);

    Integer update(ScheduleJob scheduleJob);

    Integer updateStatusWithExecTime(ScheduleJob job);

    Integer updateNullTime(@Param("jobId") String jobId);

    List<String> listFillJobName(PageQuery<ScheduleJobDTO> pageQuery);

    Integer countFillJobNameDistinct(@Param("model") ScheduleJobDTO batchJobDTO);

    Integer countFillJobNameDistinctWithOutTask(@Param("model") ScheduleJobDTO batchJobDTO);

    Integer countFillJobName(@Param("model") ScheduleJobDTO batchJobDTO);

    List<String> listFillJobBizDate(@Param("model") ScheduleJobDTO dto);

    List<ScheduleJob> listNeedStopFillDataJob(@Param("fillDataJobName") String fillDataJobName, @Param("statusList") List<Integer> statusList,
                                              @Param("projectId") Long projectId, @Param("appType") Integer appType);

    List<Map<String, Object>> listTaskExeInfo(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("limitNum") int limitNum, @Param("appType") Integer appType);

    /**
     * 根据jobId获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    List<ScheduleJob> getSubJobsAndStatusByFlowId(@Param("jobId") String jobId);

    /**
     * 获取补数据job的各状态的数量
     *
     * @param jobName
     * @return
     */
    List<Map<String, Long>> countFillDataAllStatusByJobName(@Param("jobName") String jobName, @Param("jobIds") List<String> jobIds);
//    List<Map<String, Long>> countFillDataAllStatusByJobName(@Param("jobName") String jobName);

    /**
     * 根据id获取其中工作流类型的实例
     *
     * @param ids
     * @return
     */
    List<String> getWorkFlowJobId(@Param("ids") List<Long> ids, @Param("taskTypes") List<Integer> taskTypes);

    /**
     * 根据工作流实例jobId获取全部子任务实例
     *
     * @param flowIds
     * @return
     */
    List<ScheduleJob> getSubJobsByFlowIds(@Param("flowIds") List<String> flowIds);

    Integer getTaskTypeByJobId(@Param("jobId") String jobId);

    List<String> getFlowJobIdsByJobName(@Param("jobName") String jobName);

    /**
     * 测试时使用，上线前删除
     *
     * @param jobIds
     */
    void setJobRestart(@Param("list") List<String> jobIds);

    List<Map<String, Long>> countByFillDataAllStatus(@Param("fillIdList") List<Long> fillJobIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    List<Long> listFillIdList(PageQuery<ScheduleJobDTO> pageQuer);

    List<Long> listFillIdListWithOutTask(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> queryFillData(PageQuery pageQuery);

    Integer countByFillData(@Param("model") ScheduleJobDTO batchJobDTO);

    ScheduleJob getWorkFlowTopNode(@Param("jobId") String jobId);

    Map<String, Object> countScienceJobStatus(@Param("status") Integer runStatus, @Param("projectIds") List<Long> projectIds, @Param("type") Integer type, @Param("taskTypes") List<Integer> taskTypes, @Param("tenantId") long tenantId,@Param("cycStartDay") String cycStartDay, @Param("cycEndDay") String cycEndDay);

    List<ScheduleJob> listByJobIdList(@Param("jobIds") List<String> jobIds, @Param("projectId") Long projectId);

    List<String> listJobIdByTaskType(@Param("taskType") Integer taskType);

    Integer getStatusById(@Param("id") Long id);

    Integer countTasksByCycTimeTypeAndAddress(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    List<SimpleScheduleJobPO> listSimpleJobByStatusAddress(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress);

    Integer updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("ids") List<Long> ids);

    Integer updateJobStatusByIds(@Param("status") Integer status, @Param("ids") List<Long> ids);

    void stopUnsubmitJob(@Param("likeName") String likeName, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("status") Integer status);

    List<ScheduleJob> listExecJobByCycTimeTypeAddress(@Param("startId") Long startId, @Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime, @Param("phaseStatus") Integer phaseStatus, @Param("isEq") Boolean isEq);

    Integer updateJobInfoByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("execStartTime") Timestamp execStartTime, @Param("execEndTime") Timestamp execEndTime, @Param("execTime") Long execTime, @Param("retryNum") Integer retryNum,@Param("stopStatuses") List<Integer> stopStatuses);

    ScheduleJob getByTaskIdAndStatusOrderByIdLimit(@Param("taskId") Long taskId, @Param("status") Integer status, @Param("time") Timestamp time,@Param("appType") Integer appType);

    Integer updateStatusAndLogInfoById(@Param("id") Long id, @Param("status") Integer status, @Param("logInfo") String logInfo);

    Integer updateStatusByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo);

    List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleJob> listJobByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    List<ScheduleJob> listJobByCyctimeAndJobNameBatch(@Param("startId") Long startId, @Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType, @Param("batchJobSize") Integer batchJobSize);

    Integer countJobByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    List<ScheduleJob> listJobsByTaskIdAndApptype(@Param("taskIds") List<Long> taskIds,@Param("appType") Integer appType);

    void deleteByJobKey(@Param("jobKeyList") List<String> jobKeyList);

    List<String> getAllNodeAddress();

    List<ScheduleJob> syncQueryJob(PageQuery<ScheduleJobDTO> pageQuery);


    Integer insert(ScheduleJob scheduleJob);

    void jobFail(@Param("jobId") String jobId, @Param("status") int status, @Param("logInfo") String logInfo);

    void updateJobStatus(@Param("jobId") String jobId, @Param("status") int status);

    void updateTaskStatusNotStopped(@Param("jobId") String jobId, @Param("status") int status, @Param("stopStatuses") List<Integer> stopStatuses);

    void updateJobPluginId(@Param("jobId") String jobId, @Param("pluginId") long pluginId);

    void updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);

    void updateJobSubmitSuccess(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("appId") String appId, @Param("submitLog") String submitLog,@Param("latencyMarkerInfo") String latencyMarkerInfo);

    ScheduleJob getRdosJobByJobId(@Param("jobId") String jobId);

    List<ScheduleJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

    void updateEngineLog(@Param("jobId")String jobId, @Param("engineLog")String engineLog);

    void updateRetryTaskParams(@Param("jobId")String jobId,  @Param("retryTaskParams")String retryTaskParams);

    Integer updateTaskStatusCompareOld(@Param("jobId") String jobId, @Param("status")Integer status,@Param("oldStatus") Integer oldStatus, @Param("jobName")String jobName);

    ScheduleJob getByName(@Param("jobName") String jobName);

    void updateRetryNum(@Param("jobId")String jobId, @Param("retryNum")Integer retryNum);

    List<String> getJobIdsByStatus(@Param("status")Integer status, @Param("computeType")Integer computeType);

    List<ScheduleJob> listJobStatus(@Param("time") Timestamp timeStamp, @Param("computeType")Integer computeType);

    Integer updateJobStatusByJobIds(@Param("jobIds") List<String> jobIds, @Param("status") Integer status);

    Integer updatePhaseStatusById(@Param("id") Long id, @Param("original") Integer original, @Param("update") Integer update);

    Long getListMinId(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String left, @Param("cycEndTime") String right, @Param("phaseStatus") Integer code);

    Integer updateListPhaseStatus(@Param("ids") List<Long> ids, @Param("update") Integer update);

    Integer updateJobStatusAndPhaseStatus(@Param("jobId") String jobId, @Param("status") Integer status, @Param("phaseStatus") Integer phaseStatus);

    String getLatencyMarkerInfo(@Param("jobId") String jobId);
}
