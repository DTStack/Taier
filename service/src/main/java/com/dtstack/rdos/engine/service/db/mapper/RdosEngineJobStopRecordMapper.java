package com.dtstack.rdos.engine.service.db.mapper;

import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobStopRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RdosEngineJobStopRecordMapper {

    Long insert(RdosEngineJobStopRecord rdosEngineJobStopRecord);

    List<RdosEngineJobStopRecord> getJobRetryByTaskId(@Param("taskId") String taskId);

    Integer delete(@Param("id") Long id);

    Integer updateVersion(@Param("id") Long id, @Param("version") Integer version);
}
