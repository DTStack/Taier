package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Engine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EngineDao {

    Integer insert(Engine engine);

    Engine getOne(@Param("id") Long id);

    List<Engine> listByClusterId(@Param("clusterId") Long clusterId);

    List<Engine> listByEngineIds(@Param("engineIds") List<Long> engineIds);

    Engine getByClusterIdAndEngineType(@Param("clusterId") Long clusterId, @Param("engineType") Integer engineType);

    Integer update(Engine engine);

    Integer delete(@Param("id") Long id);

    Integer updateSyncTypeByClusterIdAndEngineType(@Param("clusterId") Long clusterId, @Param("engineType") Integer engineType, @Param("syncType")Integer syncType);
}
