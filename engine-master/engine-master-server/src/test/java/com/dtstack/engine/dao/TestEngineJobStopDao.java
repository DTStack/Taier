package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobStopRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 11:25 上午 2020/11/16
 */
public interface TestEngineJobStopDao {


    //TODO
    @Insert({"insert into schedule_engine_job_cache( job_id, engine_type, compute_type, stage, job_info, node_address, job_name, job_priority, job_resource)\n" +
            "        values(#{engineJobCache.jobId}, #{engineJobCache.engineType}, #{engineJobCache.computeType}, #{engineJobCache.stage}, #{engineJobCache.jobInfo}, #{engineJobCache.nodeAddress},#{engineJobCache.jobName},#{engineJobCache.jobPriority},#{engineJobCache.jobResource})"})
    @Options(useGeneratedKeys=true, keyProperty = "engineJobCache.id", keyColumn = "id")
    void insert(@Param("engineJobStopRecord") EngineJobStopRecord engineJobStopRecord);
}
