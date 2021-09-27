package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Account;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2020-02-14
 */
public interface AccountDao {

    Integer insert(Account account);

    Account getByName(@Param("name") String name, @Param("type") Integer type);

    Account getById(@Param("id") Long id);

    Integer update(Account account);

    Account getOne(@Param("tenantId") Long tenantId,@Param("userId") Long userId,
                   @Param("accountType") Integer accountType,@Param("name") String name);


}