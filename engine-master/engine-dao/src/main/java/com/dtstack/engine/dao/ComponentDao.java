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

    List<Component> listDefaultByEngineIds(@Param("engineIds") List<Long> engineIdList);

    Component getByEngineIdAndComponentType(@Param("engineId") Long engineId, @Param("type") Integer type);


    Component getByClusterIdAndComponentType(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("componentVersion")String componentVersion);

    Long getClusterIdByComponentId(@Param("componentId") Long componentId);

    void deleteById(@Param("componentId") Long componentId);

    Integer getIdByTenantIdComponentType(@Param("tenantId") Long tenantId,@Param("componentType") Integer componentType);

    List<Component> listByTenantId(@Param("tenantId") Long tenantId);

    Component getNextDefaultComponent(@Param("engineId") Long engineId, @Param("componentTypeCode") Integer componentTypeCode,@Param("currentDeleteId") Long currentDeleteId);

    String getDefaultComponentVersionByClusterAndComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer type);

    String getDefaultComponentVersionByTenantAndComponentType(@Param("tenantId")Long tenantId,@Param("componentType")Integer componentType);
}

