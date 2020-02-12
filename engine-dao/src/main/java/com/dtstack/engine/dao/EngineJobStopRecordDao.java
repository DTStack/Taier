package com.dtstack.engine.dao;

import com.dtstack.engine.domain.EngineJobStopRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobStopRecordDao {

    Long insert(EngineJobStopRecord engineJobStopRecord);

    Integer delete(@Param("id") Long id);

    Integer updateVersion(@Param("id") Long id, @Param("version") Integer version);

    List<EngineJobStopRecord> listStopJob(@Param("startId") Long startId);

    Integer resetRecord(@Param("id") Long id);
}
