package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJobOperatorRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-07-06
 */
public interface ScheduleJobOperatorRecordDao {

    Long insert(ScheduleJobOperatorRecord engineJobStopRecord);

    Integer delete(@Param("id") Long id);

    Integer deleteByJobIdAndType(@Param("jobId") String jobId,@Param("type")Integer type);

    Integer updateOperatorExpiredVersion(@Param("id") Long id, @Param("operatorExpired") Timestamp operatorExpired, @Param("version") Integer version);

    List<ScheduleJobOperatorRecord> listStopJob(@Param("startId") Long startId);

    List<String> listByJobIds(@Param("jobIds") List<String> jobIds);

    Timestamp getJobCreateTimeById(@Param("id") Long id);

    Long insertBatch(@Param("records") Collection<ScheduleJobOperatorRecord> records);

    List<ScheduleJobOperatorRecord> listJobs(@Param("startId")Long startId, @Param("nodeAddress")String nodeAddress, @Param("type")Integer type);

    void updateNodeAddress(@Param("nodeAddress") String nodeAddress, @Param("jobIds")List<String> value);
}
