package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.po.CountFillDataJobStatusPO;
import com.dtstack.taier.dao.domain.po.JobsStatusStatisticsPO;
import com.dtstack.taier.dao.domain.po.SimpleScheduleJobPO;
import com.dtstack.taier.dao.domain.po.StatusCountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 3:05 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {

    ScheduleJob getByTaskIdAndStatusOrderByIdLimit(@Param("taskId") Long taskId, @Param("status") Integer status, @Param("time") Timestamp time, @Param("type") Integer type);

    Integer countTasksByCycTimeTypeAndAddress(@Param("nodeAddress") String nodeAddress, @Param("scheduleType") Integer scheduleType, @Param("cycStartTime") String cycStartTime, @Param("cycEndTime") String cycEndTime);

    List<ScheduleJob> getRdosJobByJobIds(@Param("jobIds") List<String> jobIds);

    ScheduleJob getByName(@Param("jobName") String jobName);

    Integer updateJobStatusByJobIds(@Param("jobIds") List<String> jobIds, @Param("status") Integer status);

    /**
     * 获得补数据实例运行的全部状态
     *
     * @param fillIdList 补数据id
     */
    List<CountFillDataJobStatusPO> countByFillIdGetAllStatus(@Param("fillIdList") Set<Long> fillIdList);


    /**
     * 统计实例状态
     *
     * @param statistics 统计的条件
     * @return 实例统计值
     */
    List<StatusCountPO> queryJobsStatusStatistics(@Param("statistics") JobsStatusStatisticsPO statistics);

    /**
     * 扫描周期实例接口
     *
     * @param startId        开始id
     * @param nodeAddress    节点
     * @param type           类型
     * @param isEq           是否查询出第一个
     * @param jobPhaseStatus 队列状态
     * @return 周期实例列表
     */
    List<ScheduleJob> listCycleJob(@Param("startId") Long startId, @Param("nodeAddress") String nodeAddress, @Param("type") Integer type, @Param("isEq") Boolean isEq, @Param("jobPhaseStatus") Integer jobPhaseStatus);

    /**
     * 扫描实例，用于容灾
     *
     * @param startId     开始id
     * @param statuses    需求查询的状态
     * @param nodeAddress 地址
     * @return 包含部分字段的job集合
     */
    List<ScheduleJob> listSimpleJobByStatusAddress(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress);


    /**
     * 查询容灾的时候的实例
     *
     * @param startId     开始id
     * @param statuses    实例状态
     * @param nodeAddress 节点
     * @param phaseStatus 入队状态
     * @return 简单的实例封装
     */
    List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(@Param("startId") Long startId, @Param("statuses") List<Integer> statuses, @Param("nodeAddress") String nodeAddress, @Param("phaseStatus") Integer phaseStatus);

    /**
     * 查询任务在给定的计划时候之前或者之后的实例
     *
     * @param taskId  任务id
     * @param isAfter true 向前查询 fales 向后查询
     * @param cycTime 计划时间
     * @param type    实例类型 周期实例，补数据实例，立即运行实例
     * @return 实例列表
     */
    List<ScheduleJob> listAfterOrBeforeJobs(@Param("taskId") Long taskId, @Param("isAfter") Boolean isAfter, @Param("cycTime") String cycTime, @Param("type") Integer type);

    /**
     * 更新实例队列状态，队列状态字段JobPhaseStatus，用于控制周期实例扫描时实例进队出队
     *
     * @param id       实例id
     * @param original 实例当前队列状态
     * @param update   实例需要变更的队列状态
     * @return 是否更新成功
     */
    Integer updatePhaseStatusById(@Param("id") Long id, @Param("original") Integer original, @Param("update") Integer update);

    /**
     * 更新实例状态
     *
     * @param jobId  实例 id
     * @param status 状态
     * @return 更新数
     */
    Integer updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);


}
