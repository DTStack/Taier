package com.dtstack.engine.dao;

import com.dtstack.engine.domain.KerberosConfig;
import org.apache.ibatis.annotations.Param;

public interface KerberosDao {

    KerberosConfig getByClusterId(@Param("clusterId") Long clusterId);

    Integer update(KerberosConfig kerberosConfig);

    Integer insert(KerberosConfig kerberosConfig);
}
