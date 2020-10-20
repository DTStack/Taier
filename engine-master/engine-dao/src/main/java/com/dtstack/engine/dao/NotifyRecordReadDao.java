package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.NotifyRecordRead;
import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotifyRecordReadDao {

    Integer insert(NotifyRecordRead notifyRecordRead);

    Integer updateReadStatus(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("userId") Long userId, @Param("appType") Integer appType, @Param("readIds") List<Long> readIds, @Param("readStatus") Integer readStatus);

    List<NotifyRecordReadDTO> listByUserId(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("userId") Long userId, @Param("appType") Integer appType, @Param("readStatus") Integer readStatus);

    Integer delete(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("userId") Long userId, @Param("appType") Integer appType, @Param("readIds") List<Long> readIds);

    NotifyRecordReadDTO getOne(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("userId") Long userId, @Param("appType") Integer type, @Param("readId") Long readId);
}