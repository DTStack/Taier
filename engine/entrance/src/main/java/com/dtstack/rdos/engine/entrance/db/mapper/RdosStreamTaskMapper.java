package com.dtstack.rdos.engine.entrance.db.mapper;

import org.apache.ibatis.annotations.Param;

import com.dtstack.rdos.engine.entrance.db.dataobject.RdosStreamTask;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosStreamTaskMapper {
	
	public void updateTaskStatus(@Param("taskId") String taskId, @Param("status") int stauts);
	
	public void updateTaskEngineIdAndStatus(@Param("taskId") String taskId,@Param("engineId") String engineId, @Param("status") int stauts);

	public void updateTaskEngineId(@Param("taskId") String taskId,@Param("engineId") String engineId);

	public RdosStreamTask getRdosTaskByTaskId(@Param("taskId")String taskId);

}
