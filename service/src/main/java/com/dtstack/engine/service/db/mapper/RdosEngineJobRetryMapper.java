package com.dtstack.engine.service.db.mapper;

import com.dtstack.engine.service.db.dataobject.RdosEngineJobRetry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineJobRetryMapper {

	void insert(RdosEngineJobRetry rdosEngineJobRetry);

	List<RdosEngineJobRetry> listJobRetryByJobId(@Param("jobId") String jobId);

	RdosEngineJobRetry getJobRetryByJobId(@Param("jobId") String jobId, @Param("retryNum") int retryNum);

    String getRetryTaskParams(@Param("jobId")String jobId, @Param("retryNum") int retryNum);

	void removeByJobId(@Param("jobId")String jobId);

	void updateEngineLog(@Param("id") long id, @Param("engineLog") String engineLog);
}
