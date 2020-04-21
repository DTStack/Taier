package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.EngineJobStopRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobStopRecordDao {

    Long insert(EngineJobStopRecord engineJobStopRecord);

    Integer delete(@Param("id") Long id);

    Integer updateOperatorExpiredVersion(@Param("id") Long id, @Param("operatorExpired") Timestamp operatorExpired, @Param("version") Integer version);

    List<EngineJobStopRecord> listStopJob(@Param("startId") Long startId, @Param("lessThanOperatorExpired") Timestamp lessThanOperatorExpired);

    List<String> listByJobIds(@Param("jobIds") List<String> jobIds);

    Timestamp getJobCreateTimeById(@Param("id") Long id);
}
