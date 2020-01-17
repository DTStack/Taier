package com.dtstack.task.dao;

import com.dtstack.task.domain.Notify;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public interface NotifyDao {
    Notify getOne(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType);

    Integer insert(Notify notify);

    Integer update(Notify notify);

    Notify getByBizTypeAndRelationIdAndName(@Param("bizType") int type, @Param("relationId") Long verifyId, @Param("name") String name, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType);

    Notify getNotifyByNameAndProjectId(@Param("bizType") int type, @Param("name") String name, @Param("projectId") Long projectId, @Param("appType") Integer appType);

    Notify getByRelationIdAndBizTypeAndName(@Param("relationId") Long relationId, @Param("bizType") Integer bizType,@Param("name") String name, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType);

}
