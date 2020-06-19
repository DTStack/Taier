package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.EngineJobRetry;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface TestEngineJobRetryDao {
    @Insert({"insert into schedule_engine_job_retry(id, gmt_create,gmt_modified,job_id," +
            "engine_job_id,status,engine_log,log_info,application_id,retry_num,retry_task_params)\n" +
            "\t   values(#{engineJobRetry.id}, now(), now()," +
            "#{engineJobRetry.jobId},#{engineJobRetry.engineJobId}," +
            "#{engineJobRetry.status},#{engineJobRetry.engineLog}," +
            "#{engineJobRetry.logInfo},#{engineJobRetry.applicationId}," +
            "#{engineJobRetry.retryNum},#{engineJobRetry.retryTaskParams})"})
    void insert(@Param("engineJobRetry") EngineJobRetry engineJobRetry);

    @Delete({"delete from schedule_engine_job_retry\n" +
            "    where id = #{id}"})
    void deleteById(@Param("id") long id);
}
