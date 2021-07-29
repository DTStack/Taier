package com.dtstack.batch.dao;

import com.dtstack.batch.domain.Tenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author sishu.yss
 *
 */
public interface TenantDao {
	Tenant getByDtUicTenantId(@Param("dtUicTenantId")long dtUicTenantId);

	Integer updateCreateUserIdById(@Param("id")long id, @Param("userId") long userId);

	Integer insert(Tenant tenant);

	Tenant getOne(long id);

	Integer update(Tenant tenant);

	List<Tenant> getByDtUicTenantIds(@Param("dtUicTenantIdSet")Set<Long> dtUicTenantIdSet);

	/**
	 * 根据 ids 查找租户信息
	 *
	 * @param ids
	 * @return
	 */
	List<Tenant> listDtuicTenantIdByTenantId(@Param("ids")List<Long> ids);


	List<Tenant> listAll();

	/**
	 * 根据id列表获取到对应的DtUicTenantId列表
	 * @param tenantIds
	 * @return
	 */
	List<Long> getDtUicTenantIdListByIds(@Param("ids")List<Long> tenantIds);
}
