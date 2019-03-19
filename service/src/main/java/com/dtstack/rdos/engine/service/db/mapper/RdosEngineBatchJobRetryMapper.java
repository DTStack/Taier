package com.dtstack.rdos.engine.service.db.mapper;

import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJobRetry;
import org.apache.ibatis.annotations.Param;

/**
 * @author toutian
 */
public interface RdosEngineBatchJobRetryMapper {

	void insert(RdosEngineBatchJobRetry rdosEngineBatchJobRetry);

	RdosEngineBatchJobRetry getJobRetryByJobId(@Param("jobId") String jobId);
}
