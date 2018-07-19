package com.dtstack.rdos.engine.service.db.mapper;

import org.apache.ibatis.annotations.Param;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

	void updateJobEngineId(@Param("jobId") String jobId, @Param("engineId") String engineId);

	void updateJobEngineIdAndStatus(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("status") int status);

	RdosEngineBatchJob getRdosJobByJobId(@Param("jobId") String jobId);

	List<RdosEngineBatchJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

	void updateEngineLog(@Param("jobId")String jobId, @Param("engineLog")String engineLog);

	void updateSubmitLog(@Param("jobId") String jobId, @Param("submitLog") String submitLog);

	List<Map<String, String>> listStatusByIds(@Param("jobIds") Collection<String> jobIdList);

	Integer updateJobStatusAndLog(@Param("jobId") String jobId, @Param("status") Integer taskStatus,
								  @Param("logInfo") String logInfo, @Param("engineLog") String engineLog,
								  @Param("gmtModified") Timestamp gmtModified);

	Integer batchInsert(@Param("list") Collection<RdosEngineBatchJob> engineJobs);

	Integer update(RdosEngineBatchJob engineJob);

	Integer finishJobWithStatus(@Param("jobId") String jobId, @Param("status") Integer taskStatus);

	Integer updateUnsubmitJobStatus(@Param("fillDataJobNameLike") String fillDataJobNameLike, @Param("status") Integer status,
									@Param("projectId") Long projectId);
}
