package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Tenant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/20
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestConsoleDtuicTenantDao {

	@Insert({"replace into console_dtuic_tenant(dt_uic_tenant_id,tenant_name,tenant_desc) VALUES(#{tenant.dtUicTenantId},#{tenant.tenantName},#{tenant.tenantDesc})"})
	void insert(@Param("tenant") Tenant tenant);

	@Delete({"delete from console_dtuic_tenant where dt_uic_tenant_id=#{dtUicTenantId}"})
	void deleteById(@Param("dtUicTenantId") Long jobId);

}
