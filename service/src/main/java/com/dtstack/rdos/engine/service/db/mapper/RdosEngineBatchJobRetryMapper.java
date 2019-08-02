package com.dtstack.rdos.engine.service.db.mapper;

import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJobRetry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineBatchJobRetryMapper {

	void insert(RdosEngineBatchJobRetry rdosEngineBatchJobRetry);

	List<RdosEngineBatchJobRetry> getJobRetryByJobId(@Param("jobId") String jobId);

    String getRetryTaskParams(@Param("jobId")String jobId, @Param("retryNum") int retrynum);

	void removeByJobId(@Param("jobId")String jobId);
}
