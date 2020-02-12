package com.dtstack.engine.mapper;

import com.dtstack.engine.domain.RdosEngineJob;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * 
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosEngineJobMapper {
	
	void insert(RdosEngineJob rdosEngineBatchJob);

	void jobFail(@Param("jobId") String jobId, @Param("status") int status, @Param("logInfo") String logInfo);
	
	void updateJobStatus(@Param("jobId") String jobId, @Param("status") int status);

	void updateTaskStatusNotStopped(@Param("jobId") String jobId, @Param("status") int status, @Param("stopStatuses") List<Integer> stopStatuses);

	void updateJobPluginId(@Param("jobId") String jobId, @Param("pluginId") long pluginId);

	void updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);

	void updateJobEngineId(@Param("jobId") String jobId, @Param("engineId") String engineId,@Param("appId") String appId);

	void updateJobEngineIdAndStatus(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("status") int status,@Param("appId") String appId);

	void updateJobSubmitFailed(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("status") int status,@Param("appId") String appId);

	RdosEngineJob getRdosJobByJobId(@Param("jobId") String jobId);

	List<RdosEngineJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

	void updateEngineLog(@Param("jobId")String jobId, @Param("engineLog")String engineLog);

	void updateRetryTaskParams(@Param("jobId")String jobId,  @Param("retryTaskParams")String retryTaskParams);

	void updateSubmitLog(@Param("jobId") String jobId, @Param("submitLog") String submitLog);

    Integer updateTaskStatusCompareOld(@Param("jobId") String jobId, @Param("status")Integer status,@Param("oldStatus") Integer oldStatus, @Param("jobName")String jobName);

	RdosEngineJob getByName(@Param("jobName") String jobName);

    List<String> listNames(@Param("jobName") String jobName);

	void updateRetryNum(@Param("jobId")String jobId, @Param("retryNum")Integer retryNum);

	Integer resetExecTime(@Param("jobId")String jobId);
	
	List<String> getTaskIdsByStatus(@Param("status")Integer status, @Param("computeType")Integer computeType);

	List<RdosEngineJob> listJobStatus(@Param("time") Timestamp timeStamp, @Param("computeType")Integer computeType);
}
