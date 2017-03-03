package com.dtstack.rdos.engine.entrance.db.mapper;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosTaskMapper {
	
	public void updateTaskStatus(String taskId,byte stauts);
	
	public void updateTaskEngineId(String taskId,String engineId);

}
