package com.dtstack.engine.dao;

import com.dtstack.engine.domain.EngineJobCache;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Date: 2020/6/20
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestEngineJobCacheDao {

	@Insert({"insert into schedule_engine_job_cache( job_id, engine_type, compute_type, stage, job_info, node_address, job_name, job_priority, job_resource, is_failover)\n" +
		"        values(#{engineJobCache.jobId}, #{engineJobCache.engineType}, #{engineJobCache.computeType}, #{engineJobCache.stage}, #{engineJobCache.jobInfo}, #{engineJobCache.nodeAddress},#{engineJobCache.jobName},#{engineJobCache.jobPriority},#{engineJobCache.jobResource},#{engineJobCache.isFailover})"})
	@Options(useGeneratedKeys=true, keyProperty = "engineJobCache.id", keyColumn = "id")
	void insert(@Param("engineJobCache") EngineJobCache engineJobCache);

	@Select({"select * from schedule_engine_job_cache limit 1"})
	EngineJobCache getOne();
}
