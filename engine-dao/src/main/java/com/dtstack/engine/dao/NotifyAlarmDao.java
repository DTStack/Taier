package com.dtstack.engine.dao;

import com.dtstack.task.domain.Notify;
import com.dtstack.task.domain.NotifyAlarm;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/11/08
 */
public interface NotifyAlarmDao {

    NotifyAlarm getByAlarmIdAndBizType(@Param("alarmId") Long alarmId, @Param("bizType") Integer bizType, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    Integer insert(NotifyAlarm notifyAlarm);

    Integer deleteByNotifyIdAlarmIdBizType(@Param("notifyId") Long notifyId, @Param("alarmId") Long alarmId, @Param("bizType") Integer bizType, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    Notify getByAlarmId(@Param("alarmId") Long alarmId, @Param("bizType") Integer bizType, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);
}
