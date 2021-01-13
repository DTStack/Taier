package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.NotifyRecordContent;
import org.apache.ibatis.annotations.Param;

public interface NotifyRecordContentDao {

    Integer insert(NotifyRecordContent notifyRecordContent);

    String getContent(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("contentId") Long contentId);
}