package com.dtstack.engine.dao;

import com.dtstack.engine.domain.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2020-02-17
 */
public interface UserDao {

    User getByDtUicUserId(@Param("dtUicUserId")Long dtUicUserId);

    User getByUserId(@Param("userId")Long userId);

    Integer insert(User user);

    Integer update(User user);

}
