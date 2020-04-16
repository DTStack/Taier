package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Account;
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

}
