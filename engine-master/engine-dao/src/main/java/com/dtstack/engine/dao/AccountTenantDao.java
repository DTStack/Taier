package com.dtstack.engine.dao;

import com.dtstack.engine.domain.AccountTenant;
import com.dtstack.engine.dto.AccountDTO;
import com.dtstack.engine.common.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-02-14
 */
public interface AccountTenantDao {

    Integer insert(AccountTenant accountTenant);

    AccountTenant getByAccount(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("accountId") Long accountId, @Param("isDeleted") Integer isDeleted);

    AccountTenant getByUserIdAndTenantIdAndEngineType(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("engineType") Integer engineType);

    Integer update(AccountTenant accountTenant);

    Integer generalCount(@Param("model") AccountDTO accountDTO);

    List<AccountDTO> generalQuery(PageQuery<AccountDTO> pageQuery);

    List<AccountDTO> getTenantUser(@Param("tenantId") Long tenantId,@Param("type") Integer type);

    AccountTenant getById(@Param("id") Long id);

}
