package com.dtstack.engine.service.db.mapper;

import com.dtstack.engine.service.db.dataobject.RdosEngineJobStopRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineJobStopRecordMapper {

    Long insert(RdosEngineJobStopRecord rdosEngineJobStopRecord);

    Integer delete(@Param("id") Long id);

    Integer updateVersion(@Param("id") Long id, @Param("version") Integer version);

    List<RdosEngineJobStopRecord> listStopJob(@Param("startId") Long startId);

    Integer resetRecord(@Param("id") Long id);

    List<String> listByJobIds(@Param("jobIds") List<String> jobIds);
}
