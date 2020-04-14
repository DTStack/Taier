package com.dtstack.engine.dao;

import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.po.SimpleBatchJobPO;
import com.dtstack.engine.dto.BatchJobDTO;
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
public interface BatchJobDao {

    BatchJob getOne(@Param("id") Long id);

    List<BatchJob> listByJobIds(@Param("jobIds") List<Long> jobIds, @Param("projectId") Long projectId);

    BatchJob getByJobKeyAndType(@Param("jobKey") String jobKey, @Param("type") int type);

    BatchJob getByJobKey(@Param("jobKey") String jobKey);

    List<Map<String, Object>> countByStatusAndType(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status);

    List<Map<String, Object>> selectStatusAndType(@Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType,
                                                  @Param("dtuicTenantId") Long dtuicTenantId, @Param("statuses") List<Integer> status, @Param("startPage") Integer startPage, @Param("pageSize") Integer pageSize);

    List<Map<String, Object>> listTopRunTime(@Param("projectId") Long projectId, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listTopErrorByType(@Param("dtuicTenantId") Long dtuicTenantId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("type") Integer type, @Param("time") Timestamp time, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery, @Param("appType") Integer appType);

    List<Map<String, Object>> listTodayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listYesterdayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listMonthJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId);

    List<Map<String, Object>> listThirtyDayJobs(@Param("statusList") List<Integer> statusList, @Param("type") Integer type, @Param("taskType") Integer taskType, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    List<BatchJob> listRestartBatchJobList(@Param("type") int type, @Param("status") Integer taskStatus, @Param("lastTime") Timestamp lastTime);

    List<BatchJob> listJobByJobKeys(@Param("jobKeys") Collection<String> jobKeys);

    List<Long> listIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("appType") Integer appType);

    List<String> listJobIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status);

    List<Map<String, String>> listTaskExeTimeInfo(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("pageQuery") PageQuery pageQuery);

    BatchJob getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    Long getId(@Param("jobId") String jobId);

    List<BatchJob> generalQuery(PageQuery<BatchJobDTO> pageQuery);

    List<BatchJob> generalQueryWithMinAndHour(PageQuery<BatchJobDTO> pageQuery);

    List<BatchJob> listAfterOrBeforeJobs(@Param("taskId") Long taskId, @Param("isAfter") boolean isAfter, @Param("cycTime") String cycTime);

    Integer generalCount(@Param("model") BatchJobDTO object);

    Integer generalCountWithMinAndHour(@Param("model") BatchJobDTO object);

    Integer minOrHourJobCount(@Param("model") BatchJobDTO object);

    List<BatchJob> minOrHourJobQuery(PageQuery<BatchJobDTO> pageQuery);


    List<Map<Integer, Long>> getJobsStatusStatistics(@Param("model") BatchJobDTO object);

    Integer insert(BatchJob batchJob);

    Integer batchInsert(Collection batchJobs);

    Integer update(BatchJob batchJob);

    Integer updateStatusWithExecTime(BatchJob job);

    Integer updateNullTime(@Param("jobId") String jobId);

    List<String> listFillJobName(PageQuery<BatchJobDTO> pageQuery);

    Integer countFillJobNameDistinct(@Param("model") BatchJobDTO batchJobDTO);

    Integer countFillJobNameDistinctWithOutTask(@Param("model") BatchJobDTO batchJobDTO);

    Integer countFillJobName(@Param("model") BatchJobDTO batchJobDTO);

    List<String> listFillJobBizDate(@Param("model") BatchJobDTO dto);

    List<BatchJob> listNeedStopFillDataJob(@Param("fillDataJobName") String fillDataJobName, @Param("statusList") List<Integer> statusList,
                                           @Param("projectId") Long projectId, @Param("appType") Integer appType);

    List<Map<String, Object>> listTaskExeInfo(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("limitNum") int limitNum, @Param("appType") Integer appType);

    /**
     * 根据jobId获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    List<BatchJob> getSubJobsAndStatusByFlowId(@Param("jobId") String jobId);

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
    List<BatchJob> getSubJobsByFlowIds(@Param("flowIds") List<String> flowIds);

    Integer getTaskTypeByJobId(@Param("jobId") String jobId);

    List<String> getFlowJobIdsByJobName(@Param("jobName") String jobName);

    /**
     * 测试时使用，上线前删除
     *
     * @param jobIds
     */
    void setJobRestart(@Param("list") List<String> jobIds);

    List<Map<String, Long>> countByFillDataAllStatus(@Param("fillIdList") List<Long> fillJobIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    List<Long> listFillIdList(PageQuery<BatchJobDTO> pageQuer);

    List<Long> listFillIdListWithOutTask(PageQuery<BatchJobDTO> pageQuery);

    List<BatchJob> queryFillData(PageQuery pageQuery);

    Integer countByFillData(@Param("model") BatchJobDTO batchJobDTO);

    BatchJob getWorkFlowTopNode(@Param("jobId") String jobId);

    Map<String, Object> countScienceJobStatus(@Param("status") Integer runStatus, @Param("projectIds") List<Long> projectIds, @Param("type") Integer type, @Param("taskType") Integer taskType, @Param("tenantId") long tenantId);

    List<BatchJob> listByJobIdList(@Param("jobIds") List<String> jobIds, @Param("projectId") Long projectId);

    List<String> listJobIdByTaskType(@Param("taskType") Integer taskType);

    Integer getStatusById(@Param("id") Long id);

    Integer countTasksByCycTimeTypeAndAddress(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    List<SimpleBatchJobPO> listSimpleJobByStatusAddress(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress);

    Integer updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("ids") List<Long> ids);

    Integer updateJobStatus(@Param("status") Integer status, @Param("ids") List<Long> ids);

    void stopUnsubmitJob(@Param("likeName") String likeName, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("status") Integer status);

    List<BatchJob> listExecJobByCycTimeTypeAddress(@Param("startId") Long startId, @Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    Integer updateJobInfoByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("execStartTime") Timestamp execStartTime, @Param("execEndTime") Timestamp execEndTime, @Param("execTime") Long execTime, @Param("retryNum") Integer retryNum,@Param("stopStatuses") List<Integer> stopStatuses);

    BatchJob getByTaskIdAndStatusOrderByIdLimit(@Param("taskId") Long taskId, @Param("status") Integer status, @Param("time") Timestamp time);

    Integer updateStatusAndLogInfoById(@Param("id") Long id, @Param("status") Integer status, @Param("logInfo") String logInfo);

    Integer updateStatusByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo);

    List<BatchJob> listByBusinessDateAndPeriodTypeAndStatusList(PageQuery<BatchJobDTO> pageQuery);

    List<BatchJob> listJobByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    List<BatchJob> listJobByCyctimeAndJobNameBatch(@Param("startId") Long startId, @Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType, @Param("batchJobSize") Integer batchJobSize);

    Integer countJobByCyctimeAndJobName(@Param("preCycTime") String preCycTime, @Param("preJobName") String preJobName, @Param("scheduleType") Integer scheduleType);

    void deleteByJobKey(@Param("jobKeyList") List<String> jobKeyList);

    List<String> getAllNodeAddress();

    List<BatchJob> syncQueryJob(PageQuery<BatchJobDTO> pageQuery);
}
