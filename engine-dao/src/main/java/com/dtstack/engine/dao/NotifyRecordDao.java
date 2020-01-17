package com.dtstack.engine.dao;

import com.dtstack.task.domain.NotifyRecord;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public interface NotifyRecordDao {

    NotifyRecord getOne(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    Integer insert(NotifyRecord notifyRecord);
}
