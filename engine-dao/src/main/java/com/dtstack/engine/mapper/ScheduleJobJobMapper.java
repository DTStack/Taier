package com.dtstack.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.domain.ScheduleJobJob;
import com.dtstack.engine.dto.ScheduleJobJobTaskDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 10:33 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobJobMapper extends BaseMapper<ScheduleJobJob> {
    List<ScheduleJobJob> listByJobKey(@Param("jobKey") String jobKey);

    List<ScheduleJobJob> listByJobKeys(@Param("list") List<String> jobKeys);

    List<ScheduleJobJob> listByParentJobKey(@Param("jobKey") String jobKey);

    Integer batchInsert(Collection batchJobJobs);

    Integer update(ScheduleJobJob scheduleJobJob);

    List<ScheduleJobJob> listSelfDependency(@Param("pjobKey") String pjobKey);

    List<ScheduleJobJob> listByParentJobKeys(@Param("list") List<String> list);

    List<ScheduleJobJobTaskDTO> listByParentJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeyList);

    List<ScheduleJobJobTaskDTO> listByJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeys);

    void deleteByJobKey(@Param("jobKeyList") List<String> jobKeyList);
}
