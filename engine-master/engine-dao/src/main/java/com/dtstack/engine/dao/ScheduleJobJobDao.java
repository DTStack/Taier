package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.dto.ScheduleJobJobTaskDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobJobDao {

    List<ScheduleJobJob> listByJobKey(@Param("jobKey") String jobKey);

    List<ScheduleJobJob> listByJobKeys(@Param("list") List<String> jobKeys);

    List<ScheduleJobJob> listByParentJobKey(@Param("jobKey") String jobKey);

    Integer insert(ScheduleJobJob scheduleJobJob);

    Integer batchInsert(Collection batchJobJobs);

    Integer update(ScheduleJobJob scheduleJobJob);

    List<ScheduleJobJob> listSelfDependency(@Param("pjobKey") String pjobKey);

    List<ScheduleJobJob> listByParentJobKeys(@Param("list") List<String> list);

    List<ScheduleJobJobTaskDTO> listByParentJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeyList);

    List<ScheduleJobJobTaskDTO> listByJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeys, @Param("selfTaskIdList") List<Long> taskIdList);

    void deleteByJobKey(@Param("jobKeyList") List<String> jobKeyList);
}
