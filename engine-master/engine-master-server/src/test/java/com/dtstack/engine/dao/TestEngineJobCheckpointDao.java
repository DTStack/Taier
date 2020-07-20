package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/14
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestEngineJobCheckpointDao {
	@Insert({"insert into schedule_engine_job_checkpoint(task_id, task_engine_id, checkpoint_id, checkpoint_trigger, checkpoint_savepath, checkpoint_counts)\n" +
		"        values(#{engineJobCheckpoint.taskId}, #{engineJobCheckpoint.taskEngineId}, #{engineJobCheckpoint.checkpointId}, #{engineJobCheckpoint.checkpointTrigger}, #{engineJobCheckpoint.checkpointSavepath}, #{engineJobCheckpoint.checkpointCounts})"})
	@Options(useGeneratedKeys=true, keyProperty = "engineJobCheckpoint.id", keyColumn = "id")
	void insert(@Param("engineJobCheckpoint") EngineJobCheckpoint engineJobCheckpoint);
}
