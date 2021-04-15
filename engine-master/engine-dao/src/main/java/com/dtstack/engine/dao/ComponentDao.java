package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Component;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComponentDao {

    Component getOne(@Param("id") Long id);

    Integer insert(Component component);

    Integer update(Component component);

    Integer updateMetadata(@Param("engineId") Long engineId, @Param("type") Integer type,@Param("isMetadata") Integer isMetadata);

    List<Component> listByEngineIds(@Param("engineIds") List<Long> engineId);

    Component getByEngineIdAndComponentType(@Param("engineId") Long engineId, @Param("type") Integer type);

    Component getByClusterIdAndComponentType(@Param("clusterId") Long clusterId, @Param("type") Integer type);

    Long getClusterIdByComponentId(@Param("componentId") Long componentId);

    void deleteById(@Param("componentId") Long componentId);

    Integer getIdByTenantIdComponentType(@Param("tenantId") Long tenantId,@Param("componentType") Integer componentType);

    List<Component> listByTenantId(@Param("tenantId") Long tenantId);
}
