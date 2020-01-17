package com.dtstack.engine.dao;

import com.dtstack.task.domain.BatchAlarmRecordUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2019-11-01
 */
public interface BatchAlarmRecordUserDao {

    List<Long> getAlarmByRecordUserId(@Param("userId")Long userId, @Param("appType")Integer appType, @Param("projectId")Long projectId);

    Long insert(BatchAlarmRecordUser batchAlarmRecordUser);

    Long batchInsert(List<BatchAlarmRecordUser> batchAlarmRecordUser);

    List<Long> getAlarmUserIdByRecordId(@Param("alarmRecordId") Long recordId, @Param("appType")Integer appType, @Param("projectId")Long projectId);
}
