package com.dtstack.engine.dao;


import com.dtstack.engine.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-02-17
 */
public interface UserDao {

    User getByDtUicUserId(@Param("dtUicUserId") Long dtUicUserId);

    User getByUserId(@Param("userId") Long userId);

    Integer insert(User user);

    Integer update(User user);

    List<User> getByDtUicUserIds(@Param("userIds") Collection<Long> userIds);

    Integer insertBatch(@Param("users") List<User> users);

    List<User> getAllUser();

    List<User> listByIds(@Param("userIds") Collection<Long> ids);

    List<User> listByNotInIdsAndName(@Param("ids") List<Long> userInIds, @Param("userName") String name);

    void batchInsert(@Param("userList") List<User> addUserList);

    /**
     * 根据用户名like 过滤出用户信息
     *
     * @param userName
     * @return
     */
    List<User> getUsersByUserNameAndUserIds(@Param("userIds") List<Long> userIds, @Param("userName") String userName);

    /**
     * 根据用户名like 过滤出用户信息
     *
     * @param userName
     * @return
     */
    List<User> getUsersByUserName(@Param("userName") String userName);
}
