package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Tenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TenantDao {

    Integer insert(Tenant tenant);

    List<Long> listAllDtUicTenantIds();

    String getNameByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    Long getIdByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    Tenant getByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    List<Long> listDtUicTenantIdByIds(@Param("ids") List<Long> ids);

    List<Tenant> listAllTenantByDtUicTenantIds(@Param("ids") List<Long> ids);

    void delete(@Param("id")Long id);

    void updateByDtUicTenantId(Tenant tenant);
}
