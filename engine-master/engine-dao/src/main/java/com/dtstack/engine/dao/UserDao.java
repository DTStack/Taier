package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author yuebai
 * @date 2020-02-17
 */
public interface UserDao {

    User getByDtUicUserId(@Param("dtUicUserId") Long dtUicUserId);

    User getByUserId(@Param("userId") Long userId);

    Integer insert(User user);

    Integer update(User user);

    List<User> getByDtUicUserIds(@Param("userIds") Set<Long> userIds);

    Integer insertBatch(@Param("users") List<User> users);

    List<User> getAllUser();
}
