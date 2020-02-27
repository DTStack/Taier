package com.dtstack.engine.dao;

import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.engine.domain.EngineTenant;
import com.dtstack.engine.vo.EngineTenantVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EngineTenantDao {

    List<Long> listEngineIdByTenantId(@Param("tenantId") Long tenantId);

    Integer insert(EngineTenant engineTenant);

    Integer updateQueueId(@Param("tenantId") Long tenantId, @Param("engineId") Long engineId, @Param("queueId") Long queueId);

    Integer generalCount(@Param("engineId") Long engineId, @Param("tenantName") String tenantName);

    List<EngineTenantVO> generalQuery(@Param("query") PageQuery<Object> query, @Param("engineId") Long engineId, @Param("tenantName") String tenantName);

    List<EngineTenant> listByEngineIds(@Param("engineIds") List<Long> engineIds);

    List<Long> listTenantIdByQueueIds(@Param("queueIds") List<Long> queueIds);

    Long getQueueIdByTenantId(@Param("tenantId") Long tenantId);

    List<EngineTenantVO> listEngineTenant(Long engineId);

    EngineTenant getByTenantIdAndEngineType(@Param("dtuicTenantId") Long dtuicTenantId, @Param("engineType")Integer engineType);

    Long getClusterIdByTenantId(@Param("tenantId") Long tenantId);
}

