package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.AlertRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:43 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlertRecordDao {

    Integer insert(@Param("records") List<AlertRecord> alertRecords);

    Integer updateByMapAndIds(@Param("record") AlertRecord alertRecord, @Param("params") Map<String, Object> params, @Param("recordIds") List<Long> recordIds);

    Integer updateByMap(@Param("record") AlertRecord record, @Param("params") Map<String, Object> params);

    List<AlertRecord> selectQuery(@Param("record") AlertRecord queryAlertRecord);

    Long findMinIdByStatus(@Param("sendStatus") Integer sendStatus, @Param("nodeAddress") String nodeAddress, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    List<AlertRecord> findListByStatus(@Param("sendStatusList") List<Integer> sendStatusList, @Param("nodeAddress") String nodeAddress, @Param("startDate") Long startDate, @Param("endDate") Long endDate, @Param("minId") Long minId, @Param("alertRecordSendStatus") Integer alertRecordSendStatus);
}
