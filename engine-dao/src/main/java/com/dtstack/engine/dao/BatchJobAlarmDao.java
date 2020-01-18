package com.dtstack.engine.dao;

import com.dtstack.engine.domain.BatchJobAlarm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchJobAlarmDao {

    Integer insert(BatchJobAlarm batchJobAlarm);

    Integer countByJobId(@Param("jobId") long jobId);

    Integer update(BatchJobAlarm batchJobAlarm);

    Integer deleteFinishedJob(@Param("statuses") List<Integer> statuses);

    List<BatchJobAlarm> getByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType);
}
