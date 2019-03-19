package com.dtstack.rdos.engine.service.db.mapper;

import org.apache.ibatis.annotations.Param;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;

import java.util.List;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosEngineBatchJobMapper {
	
	void insert(RdosEngineBatchJob rdosEngineBatchJob);

	void jobFail(@Param("jobId") String jobId, @Param("status") int status, @Param("logInfo") String logInfo);
	
	void updateJobStatus(@Param("jobId") String jobId, @Param("status") int status);

	void updateJobPluginId(@Param("jobId") String jobId, @Param("pluginId") long pluginId);

	void updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);

	void updateJobEngineId(@Param("jobId") String jobId, @Param("engineId") String engineId,@Param("appId") String appId);

	void updateJobEngineIdAndStatus(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("status") int status,@Param("appId") String appId, @Param("updateStartTime") String updateStartTime);

	RdosEngineBatchJob getRdosJobByJobId(@Param("jobId") String jobId);

	List<RdosEngineBatchJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

	void updateEngineLog(@Param("jobId")String jobId, @Param("engineLog")String engineLog);

	void updateSubmitLog(@Param("jobId") String jobId, @Param("submitLog") String submitLog);

    Integer updateTaskStatusCompareOld(@Param("jobId") String jobId, @Param("status")Integer status,@Param("oldStatus") Integer oldStatus, @Param("jobName")String jobName);

	RdosEngineBatchJob getByName(@Param("jobName") String jobName);

    List<String> listNames(@Param("jobName") String jobName);

	void updateRetryNum(@Param("jobId")String jobId, @Param("retryNum")Integer retryNum);
}
