package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.KerberosConfig;
import org.apache.ibatis.annotations.Param;

public interface KerberosDao {

    KerberosConfig getByClusterId(@Param("clusterId") Long clusterId);

    KerberosConfig getByComponentId(@Param("componentId") Long componentId);

    Integer update(KerberosConfig kerberosConfig);

    Integer insert(KerberosConfig kerberosConfig);

    void delete(@Param("componentId") Long componentId);
}
