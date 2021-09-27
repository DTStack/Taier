package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Tenant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestTenantDao
 * @Description TODO
 * @Date 2020/11/25 14:04
 * @Created chener@dtstack.com
 */
public interface TestTenantDao {

    @Insert({"INSERT INTO console_dtuic_tenant(id,dt_uic_tenant_id,tenant_name,tenant_desc)VALUES(#{tenant.id},#{tenant.dtUicTenantId},#{tenant.tenantName},#{tenant.tenantDesc})"})
    @Options(useGeneratedKeys=true, keyProperty = "tenant.id", keyColumn = "id")
    Integer insert(@Param("tenant") Tenant tenant);

}
