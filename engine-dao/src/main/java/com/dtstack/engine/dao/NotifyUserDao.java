package com.dtstack.engine.dao;

import com.dtstack.engine.domain.NotifyUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public interface NotifyUserDao {

    Integer insert(NotifyUser notifyUser);

    List<Long> getUserIdByNotifyId(@Param("notifyId") Long notifyId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    Integer deleteByNotifyIdAndUserIds(@Param("notifyId") Long notifyId, @Param("userIds") List<Long> userIds, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    List<Long> listUserIdByNotifyId(@Param("notifyId") long notifyId);
}
