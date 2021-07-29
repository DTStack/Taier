package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskTemplate;
import org.apache.ibatis.annotations.Param;

public interface BatchTaskTemplateDao {

    Integer update(BatchTaskTemplate batchAlarm);

    String getContentByType(@Param("taskType") Integer taskType, @Param("type") Integer type);
}
