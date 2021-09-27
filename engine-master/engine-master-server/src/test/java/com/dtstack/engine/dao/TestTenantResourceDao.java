package com.dtstack.engine.dao;

import com.dtstack.engine.domain.TenantResource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestTenantResourceDao
 * @Description TODO
 * @Date 2020/11/25 15:26
 * @Created chener@dtstack.com
 */
public interface TestTenantResourceDao {

    @Insert({"INSERT INTO console_tenant_resource(tenant_id,dt_uic_tenant_id,task_type,engine_type,resource_limit)VALUES(#{tenantResource.tenantId},#{tenantResource.dtUicTenantId},#{tenantResource.taskType},#{tenantResource.engineType},#{tenantResource.resourceLimit})"})
    @Options(useGeneratedKeys=true, keyProperty = "tenantResource.id", keyColumn = "id")
    Integer insert(@Param("tenantResource") TenantResource tenantResource);

}
