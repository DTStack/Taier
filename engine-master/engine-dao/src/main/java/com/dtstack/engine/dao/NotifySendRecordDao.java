package com.dtstack.engine.dao;


import com.dtstack.engine.api.domain.NotifySendRecord;
import org.apache.ibatis.annotations.Param;

public interface NotifySendRecordDao {

    Integer insert(NotifySendRecord notifySendRecord);

    Integer updateByIdAndStatus(@Param("id") Long id, @Param("status") Integer status);
}