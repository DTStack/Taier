package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.KerberosConfig;
import org.apache.ibatis.annotations.Param;

public interface KerberosDao {

    KerberosConfig getByComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    Integer update(KerberosConfig kerberosConfig);

    Integer insert(KerberosConfig kerberosConfig);

    void delete(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    void deleteByComponentId(@Param("componentId") Long componentId);
}
