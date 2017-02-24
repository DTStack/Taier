package com.dtstack.rdos.engine.entrance.db.mapper;

public interface RdosTaskMapper {
	
	public void updateTaskStatus(String taskId,byte stauts);
	
	public void updateTaskEngineId(String taskId,String engineId);

}
