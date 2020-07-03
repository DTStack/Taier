package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.EngineJobCache;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/20
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestEngineJobCacheDao {

	@Insert({"insert into schedule_engine_job_cache( job_id, engine_type, compute_type, stage, job_info, node_address, job_name, job_priority, job_resource)\n" +
		"        values(#{engineJobCache.jobId}, #{engineJobCache.engineType}, #{engineJobCache.computeType}, #{engineJobCache.stage}, #{engineJobCache.jobInfo}, #{engineJobCache.nodeAddress},#{engineJobCache.jobName},#{engineJobCache.jobPriority},#{engineJobCache.jobResource})"})
	@Options(useGeneratedKeys=true, keyProperty = "engineJobCache.id", keyColumn = "id")
	void insert(@Param("engineJobCache") EngineJobCache engineJobCache);
}
