package com.dtstack.engine.dao;

import com.dtstack.engine.domain.JobGraphTrigger;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface JobGraphTriggerDao {

    Integer insert(JobGraphTrigger jobGraphTrigger);

    JobGraphTrigger getByTriggerTimeAndTriggerType(@Param("triggerTime") Timestamp timestamp, @Param("triggerType") int triggerType);

    /**
     * 测试时使用，上线前删除
     */
    void deleteToday();
}
