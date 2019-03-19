package com.dtstack.rdos.engine.service.db.mapper;

import org.apache.ibatis.annotations.Param;

import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;

import java.util.List;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosEngineStreamJobMapper {

    void insert(RdosEngineStreamJob rdosEngineStreamJob);

	void updateTaskStatus(@Param("taskId") String taskId, @Param("status") int stauts);

	void updateTaskPluginId(@Param("taskId") String taskId, @Param("pluginId") long pluginId);

	void updateTaskEngineIdAndStatus(@Param("taskId") String taskId,@Param("engineId") String engineId, @Param("applicationId") String applicationId, @Param("status") int status, @Param("updateStartTime") String updateStartTime);

	void updateTaskEngineId(@Param("taskId") String taskId, @Param("engineId") String engineId, @Param("applicationId") String applicationId);

	RdosEngineStreamJob getRdosTaskByTaskId(@Param("taskId")String taskId);

	List<RdosEngineStreamJob> getRdosTaskByTaskIds(@Param("taskIds")List<String> taskIds);

	void updateEngineLog(@Param("taskId")String taskId, @Param("engineLog")String engineLog);

	void updateSubmitLog(@Param("taskId")String taskId, @Param("submitLog")String submitLog);

	void submitFail(@Param("taskId") String taskId,@Param("status") Integer status,@Param("submitLog") String s);

	Integer updateTaskStatusCompareOld(@Param("taskId") String taskId, @Param("status")Integer status,@Param("oldStatus") Integer oldStatus, @Param("taskName") String taskName);

	RdosEngineStreamJob getByName(@Param("taskName") String taskName);

    List<String> listNames(@Param("taskName") String taskName);

	List<String> getTaskIdsByStatus(@Param("status") Integer status);

	void updateRetryNum(@Param("taskId")String taskId, @Param("retryNum")Integer retryNum);
}
