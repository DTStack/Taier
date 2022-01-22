package com.dtstack.taiga.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.po.CountFillDataJobStatusPO;
import com.dtstack.taiga.dao.domain.po.JobsStatusStatisticsPO;
import com.dtstack.taiga.dao.domain.po.SimpleScheduleJobPO;
import com.dtstack.taiga.dao.domain.po.StatusCountPO;
import com.dtstack.taiga.dao.dto.ScheduleJobDTO;
import com.dtstack.taiga.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.*;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 3:05 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {

    ScheduleJob getOne(@Param("id") Long id);
    /**
     * 获得补数据实例运行的全部状态
     *
     * @param fillIdList   补数据id
     * @return
     */
    List<CountFillDataJobStatusPO> countByFillIdGetAllStatus(@Param("fillIdList") Set<Long> fillIdList);

    ScheduleJob getByTaskIdAndStatusOrderByIdLimit(@Param("taskId") Long taskId, @Param("status") Integer status, @Param("time") Timestamp time, @Param("type") Integer type);

    List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress, @Param("phaseStatus") Integer phaseStatus);

    Integer updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);

    List<StatusCountPO> queryJobsStatusStatistics(@Param("statistics") JobsStatusStatisticsPO statistics);

    List<ScheduleJob> listAfterOrBeforeJobs(@Param("taskId") Long taskId, @Param("isAfter") Boolean isAfter, @Param("cycTime") String cycTime, @Param("type") Integer type);

    int countByCycTimeAndJobName(@Param("cycTime") String cycTime, @Param("jobName") String jobName, @Param("type") Integer type);

    List<ScheduleJob> listByCycTimeAndJobName(@Param("startId") Long startId, @Param("cycTime") String cycTime, @Param("jobName") String jobName, @Param("type") Integer type, @Param("jobSize") Integer jobSize);

    ScheduleJob getByJobKeyAndType(@Param("jobKey") String jobKey, @Param("type") int type);

    List<ScheduleJob> listJobByJobKeys(@Param("jobKeys") Collection<String> jobKeys);

    List<ScheduleJob> listIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("statuses") List<Integer> status, @Param("appType") Integer appType,@Param("cycTime") String cycTime,@Param("type") Integer type);

    List<String> listJobIdByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("statuses") List<Integer> status);

    ScheduleJob getByJobId(@Param("jobId") String jobId, @Param("isDeleted") Integer isDeleted);

    List<ScheduleJob> generalQuery(PageQuery<ScheduleJobDTO> pageQuery);

    Integer generalCount(@Param("model") ScheduleJobDTO object);

    Integer updateStatusWithExecTime(ScheduleJob job);

    List<Map<String, Object>> listTaskExeInfo(@Param("taskId") Long taskId, @Param("limitNum") int limitNum);

    /**
     * 根据jobId获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    List<ScheduleJob> getSubJobsAndStatusByFlowId(@Param("jobId") String jobId);

    List<ScheduleJob> listByJobIdList(@Param("jobIds") Collection<String> jobIds, @Param("projectId") Long projectId);

    Integer getStatusByJobId(@Param("jobId") String jobId);

    Integer countTasksByCycTimeTypeAndAddress(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    List<SimpleScheduleJobPO> listSimpleJobByStatusAddress(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress);

    Integer updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("jobIds") List<String> ids);

    List<ScheduleJob> listExecJobByCycTimeTypeAddress(@Param("startId") Long startId, @Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime, @Param("phaseStatus") Integer phaseStatus,
                                                      @Param("isEq") Boolean isEq, @Param("lastTime") Timestamp lastTime,@Param("isRestart") Integer isRestart);

    List<ScheduleJob> listExecJobByJobIds(@Param("nodeAddress") String nodeAddress,@Param("phaseStatus") Integer phaseStatus,@Param("isRestart") Integer isRestart,@Param("jobIds") Collection<String> jobIds);

    Integer updateStatusByJobId(@Param("jobId") String jobId, @Param("status") Integer status, @Param("logInfo") String logInfo, @Param("versionId") Integer versionId, @Param("execStartTime") Date execStartTime, @Param("execEndTime") Date execEndTime);

    void jobFail(@Param("jobId") String jobId, @Param("status") int status, @Param("logInfo") String logInfo);

    List<ScheduleJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

    ScheduleJob getByName(@Param("jobName") String jobName);

    Integer updateJobStatusByJobIds(@Param("jobIds") List<String> jobIds, @Param("status") Integer status);

    Integer updatePhaseStatusById(@Param("id") Long id, @Param("original") Integer original, @Param("update") Integer update);

    Integer updateListPhaseStatus(@Param("jobIds") List<String> ids, @Param("update") Integer update);

    /**
     * 扫描周期实例接口
     *
     * @param startSort 开始id
     * @param nodeAddress 节点
     * @param type 类型
     * @param isEq 是否查询出第一个
     * @param jobPhaseStatus 队列状态
     * @return 周期实例列表
     */
    List<ScheduleJob> listCycleJob(@Param("startSort") Long startSort, @Param("nodeAddress") String nodeAddress, @Param("type") Integer type, @Param("isEq") Boolean isEq, @Param("jobPhaseStatus") Integer jobPhaseStatus);
}
