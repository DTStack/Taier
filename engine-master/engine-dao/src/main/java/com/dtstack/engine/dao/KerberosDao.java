package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.KerberosConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KerberosDao {

    KerberosConfig getByComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType,@Param("componentVersion")String componentVersion);

    List<KerberosConfig> getByClusters(@Param("clusterId") Long clusterId);

    List<KerberosConfig> listAll();

    Integer update(KerberosConfig kerberosConfig);

    Integer insert(KerberosConfig kerberosConfig);

    void deleteByComponentId(@Param("componentId") Long componentId);

    void deleteByComponent(@Param("engineId")Long engineId,@Param("componentTypeCode")Integer componentTypeCode,@Param("componentVersion")String componentVersion);
}
