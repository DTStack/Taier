package com.dtstack.task.dao;


import com.dtstack.task.domain.BatchFillDataJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchFillDataJobDao {

    BatchFillDataJob getByJobName(@Param("jobName") String jobName, @Param("projectId") long projectId);

    Integer insert(BatchFillDataJob fillDataJob);

    List<BatchFillDataJob> listFillJob(@Param("nameList") List<String> nameList, @Param("projectId") long projectId);

    List<BatchFillDataJob> getFillJobList(@Param("fillIdList") List<Long> fillIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);
}
