package com.dtstack.batch.dao;

import com.dtstack.engine.api.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author sishu.yss
 */
@Component("batchUserDao")
public interface UserDao {

    User getByDtUicUserId(@Param("dtUicUserId") long dtUicUserId);

    List<User> listByIds(@Param("userIds") Collection<Long> userIds);

    /**
     * 获取用户信息通过uic 用户 ID 列表
     *
     * @param dtUicUserIds
     * @return
     */
    List<User> listByDtuicUserIds(@Param("dtuicUserIds") List<Long> dtUicUserIds);

    List<User> listUsersIncludeDeletedByIds(@Param("userIds") Collection<Long> userIds);

    User getOne(@Param("id") long id);

    List<User> getUserByUserName(@Param("userName") String userName);

    /**
     * 根据用户名like 过滤出用户信息
     *
     * @param userName
     * @return
     */
    List<User> getUsersByUserName(@Param("userName") String userName);

    List<User> getUserByUserNameAndProjectId(@Param("userName") String userName, @Param("projectId")Long projectId);

    List<User> listByNotInIdsAndName(@Param("ids") List<Long> userInIds, @Param("userName") String name);

    Integer insert(User user);

    /**
     * 批量添加用户信息
     *
     * @param userList
     */
    void batchInsert(@Param("userList") List<User> userList);

    Integer update(User user);

    List<User> listUserAdmin(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    /**
     * 根据用户名like 过滤出用户信息
     *
     * @param userName
     * @return
     */
    List<User> getUsersByUserNameAndUserIds(@Param("userIds") List<Long> userIds, @Param("userName") String userName);
}
