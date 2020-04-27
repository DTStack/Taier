package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.EngineJobRetry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobRetryDao {

    void insert(EngineJobRetry engineJobRetry);

    List<EngineJobRetry> listJobRetryByJobId(@Param("jobId") String jobId);

    EngineJobRetry getJobRetryByJobId(@Param("jobId") String jobId, @Param("retryNum") int retryNum);

    String getRetryTaskParams(@Param("jobId")String jobId, @Param("retryNum") int retryNum);

    void removeByJobId(@Param("jobId")String jobId);

    void updateEngineLog(@Param("id") long id, @Param("engineLog") String engineLog);
}
