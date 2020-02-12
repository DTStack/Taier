package com.dtstack.engine.dao;

import com.dtstack.engine.domain.EngineJob;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobDao {

	void insert(EngineJob rdosEngineBatchJob);

	void jobFail(@Param("jobId") String jobId, @Param("status") int status, @Param("logInfo") String logInfo);

	void updateJobStatus(@Param("jobId") String jobId, @Param("status") int status);

	void updateTaskStatusNotStopped(@Param("jobId") String jobId, @Param("status") int status, @Param("stopStatuses") List<Integer> stopStatuses);

	void updateJobPluginId(@Param("jobId") String jobId, @Param("pluginId") long pluginId);

	void updateJobStatusAndExecTime(@Param("jobId") String jobId, @Param("status") int status);

	void updateJobEngineId(@Param("jobId") String jobId, @Param("engineId") String engineId,@Param("appId") String appId);

	void updateJobEngineIdAndStatus(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("status") int status,@Param("appId") String appId);

	void updateJobSubmitFailed(@Param("jobId") String jobId, @Param("engineId") String engineId, @Param("status") int status,@Param("appId") String appId);

	EngineJob getRdosJobByJobId(@Param("jobId") String jobId);

	List<EngineJob> getRdosJobByJobIds(@Param("jobIds")List<String> jobIds);

	void updateEngineLog(@Param("jobId")String jobId, @Param("engineLog")String engineLog);

	void updateRetryTaskParams(@Param("jobId")String jobId,  @Param("retryTaskParams")String retryTaskParams);

	void updateSubmitLog(@Param("jobId") String jobId, @Param("submitLog") String submitLog);

	Integer updateTaskStatusCompareOld(@Param("jobId") String jobId, @Param("status")Integer status,@Param("oldStatus") Integer oldStatus, @Param("jobName")String jobName);

	EngineJob getByName(@Param("jobName") String jobName);

	List<String> listNames(@Param("jobName") String jobName);

	void updateRetryNum(@Param("jobId")String jobId, @Param("retryNum")Integer retryNum);

	Integer resetExecTime(@Param("jobId")String jobId);

	List<String> getTaskIdsByStatus(@Param("status")Integer status, @Param("computeType")Integer computeType);

	List<EngineJob> listJobStatus(@Param("time") Timestamp timeStamp, @Param("computeType")Integer computeType);

}
