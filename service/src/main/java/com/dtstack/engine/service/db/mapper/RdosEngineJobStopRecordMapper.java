package com.dtstack.engine.service.db.mapper;

import com.dtstack.engine.service.db.dataobject.RdosEngineJobStopRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineJobStopRecordMapper {

    Long insert(RdosEngineJobStopRecord rdosEngineJobStopRecord);

    Integer delete(@Param("id") Long id);

    Integer updateOperatorExpiredVersion(@Param("id") Long id, @Param("operatorExpired") Timestamp operatorExpired, @Param("version") Integer version);

    List<RdosEngineJobStopRecord> listStopJob(@Param("startId") Long startId, @Param("lessThanOperatorExpired") Timestamp lessThanOperatorExpired);

    Integer resetRecord(@Param("id") Long id);

    List<String> listByJobIds(@Param("jobIds") List<String> jobIds);
}
