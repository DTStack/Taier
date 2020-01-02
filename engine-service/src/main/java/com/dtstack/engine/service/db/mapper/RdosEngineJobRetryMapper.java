package com.dtstack.engine.service.db.mapper;

import com.dtstack.engine.service.db.dataobject.RdosEngineJobRetry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineJobRetryMapper {

	void insert(RdosEngineJobRetry rdosEngineJobRetry);

	List<RdosEngineJobRetry> getJobRetryByJobId(@Param("jobId") String jobId);

    String getRetryTaskParams(@Param("jobId")String jobId, @Param("retryNum") int retrynum);

	void removeByJobId(@Param("jobId")String jobId);
}
