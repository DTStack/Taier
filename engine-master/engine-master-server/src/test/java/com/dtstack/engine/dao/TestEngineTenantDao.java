package com.dtstack.engine.dao;

import com.dtstack.engine.domain.EngineTenant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestTenantDao
 * @Description 测试console_engine_tenant表
 * @Date 2020/11/24 19:21
 * @Created chener@dtstack.com
 */
public interface TestEngineTenantDao {

    @Insert({"INSERT INTO console_engine_tenant(engine_id,tenant_id,queue_id)VALUES(#{engineTenant.engineId},#{engineTenant.tenantId},#{engineTenant.queueId})"})
    @Options(useGeneratedKeys=true, keyProperty = "engineTenant.id", keyColumn = "id")
    Integer insert(@Param("engineTenant") EngineTenant engineTenant);
}
