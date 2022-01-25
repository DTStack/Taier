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

    Integer countTasksByCycTimeTypeAndAddress(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    List<SimpleScheduleJobPO> listSimpleJobByStatusAddress(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress);

    Integer updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("jobIds") List<String> ids);

    void jobFail(@Param("jobId") String jobId, @Param("status") int status, @Param("logInfo") String logInfo);

    List<ScheduleJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

    ScheduleJob getByName(@Param("jobName") String jobName);

    Integer updateJobStatusByJobIds(@Param("jobIds") List<String> jobIds, @Param("status") Integer status);

    Integer updatePhaseStatusById(@Param("id") Long id, @Param("original") Integer original, @Param("update") Integer update);

    Integer updateListPhaseStatus(@Param("jobIds") List<String> ids, @Param("update") Integer update);

    /**
     * 扫描周期实例接口
     *
     * @param startId 开始id
     * @param nodeAddress 节点
     * @param type 类型
     * @param isEq 是否查询出第一个
     * @param jobPhaseStatus 队列状态
     * @return 周期实例列表
     */
    List<ScheduleJob> listCycleJob(@Param("startId") Long startId, @Param("nodeAddress") String nodeAddress, @Param("type") Integer type, @Param("isEq") Boolean isEq, @Param("jobPhaseStatus") Integer jobPhaseStatus);
}
