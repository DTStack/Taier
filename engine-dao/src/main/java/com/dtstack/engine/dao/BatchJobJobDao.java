package com.dtstack.engine.dao;

import com.dtstack.task.domain.BatchJobJob;
import com.dtstack.task.dto.BatchJobJobTaskDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchJobJobDao {

    List<BatchJobJob> listByJobKey(@Param("jobKey") String jobKey);

    List<BatchJobJob> listByJobKeys(@Param("list") List<String> jobKeys);

    List<BatchJobJob> listByParentJobKey(@Param("jobKey") String jobKey);

    Integer insert(BatchJobJob batchJobJob);

    Integer batchInsert(Collection batchJobJobs);

    Integer update(BatchJobJob batchJobJob);

    List<BatchJobJob> listSelfDependency(@Param("pjobKey") String pjobKey);

    List<BatchJobJob> listByParentJobKeys(@Param("list") List<String> list);

    List<BatchJobJobTaskDTO> listByParentJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeyList, @Param("selfTaskIdList") List<Long> selfTaskIdList);

    List<BatchJobJobTaskDTO> listByJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeys, @Param("selfTaskIdList") List<Long> taskIdList);
}
