/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.dao;


import com.dtstack.engine.domain.EngineJobCheckpoint;
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
