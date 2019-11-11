package com.dtstack.engine.dtscript.service.db.mapper;

import com.dtstack.engine.dtscript.service.db.dataobject.RdosEngineJobStopRecord;
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
}
