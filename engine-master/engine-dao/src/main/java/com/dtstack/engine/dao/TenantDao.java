package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Tenant;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface TenantDao {

    Integer insert(Tenant tenant);

    List<Tenant> listAllDtUicTenantIds();

    String getNameByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    Long getIdByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    Tenant getByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    List<Long> listDtUicTenantIdByIds(@Param("ids") List<Long> ids);

    List<Tenant> listAllTenantByDtUicTenantIds(@Param("ids") List<Long> ids);

    void delete(@Param("dtUicTenantId") Long dtUicTenantId);

    void updateByDtUicTenantId(Tenant tenant);

    Tenant getOne(@Param("id") Long id);

    List<Long> getDtUicTenantIdListByIds(@Param("ids") List<Long> tenantIds);

    List<Tenant> getByDtUicTenantIds(@Param("dtUicTenantIds") Collection<Long> dtUicTenantIds);

    /**
     * 根据 ids 查找租户信息
     *
     * @param ids
     * @return
     */
    List<Tenant> listDtuicTenantIdByTenantId(@Param("ids")List<Long> ids);
}
