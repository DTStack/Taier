package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Tenant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/20
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestConsoleDtuicTenantDao {

	@Insert({"insert into console_dtuic_tenant(dt_uic_tenant_id,tenant_name,tenant_desc) VALUES(#{tenant.dtUicTenantId},#{tenant.tenantName},#{tenant.tenantDesc})"})
	@Options(useGeneratedKeys=true, keyProperty = "tenant.id", keyColumn = "id")
	void insert(@Param("tenant") Tenant tenant);

}
