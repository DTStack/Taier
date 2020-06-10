package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJob;
import org.apache.ibatis.annotations.Param;


public interface TestScheduleJobDao {
    void insert(@Param("scheduleJob") ScheduleJob job);
    void deleteById(@Param("id") Long id);
}
