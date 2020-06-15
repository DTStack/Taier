package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/14
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestEngineJobCheckpointDao {
	@Insert({"replace into schedule_engine_job_checkpoint( task_id, task_engine_id, checkpoint_id, checkpoint_trigger, checkpoint_savepath, checkpoint_counts)\n" +
		"        values(#{taskId}, #{engineTaskId}, #{checkpointId}, #{checkpointTrigger}, #{checkpointSavepath}, #{checkpointCounts})"})
	void insert(@Param("engineJobCheckpoint") EngineJobCheckpoint engineJobCheckpoint);

	@Delete({"delete from schedule_engine_job_checkpoint\n" +
		"    where id = #{id}"})
	void deleteById(@Param("id") Long id);
}
