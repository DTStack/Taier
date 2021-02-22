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


    @Insert({"insert into schedule_engine_job_stop_record(task_id, task_type, engine_type, compute_type, job_resource, force_cancel_flag)\n" +
            " values(#{stopRecord.taskId}, #{stopRecord.taskType}, #{stopRecord.engineType}, #{stopRecord.computeType}," +
            " #{stopRecord.jobResource}, #{stopRecord.forceCancelFlag})"})
    @Options(useGeneratedKeys=true, keyProperty = "stopRecord.id", keyColumn = "id")
    void insert(@Param("stopRecord") EngineJobStopRecord engineJobStopRecord);
}
