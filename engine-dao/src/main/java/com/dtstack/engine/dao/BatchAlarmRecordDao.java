package com.dtstack.engine.dao;


import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.engine.domain.BatchAlarmRecord;
import com.dtstack.engine.dto.BatchAlarmRecordDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchAlarmRecordDao {

    Integer countAlarmToday(@Param("projectId") long projectId,@Param("appType") Integer appType);

    Integer countAlarmWeek(@Param("projectId") long projectId,@Param("appType") Integer appType);

    Integer countAlarmMonth(@Param("projectId") long projectId,@Param("appType") Integer appType);

    List<BatchAlarmRecord> generalQuery(PageQuery<BatchAlarmRecordDTO> pageQuery);

    BatchAlarmRecord getOne(@Param("id") Long id);

    Integer generalCount(@Param("model") BatchAlarmRecordDTO query);

    List<Map<String, Object>> listByCondition(PageQuery<BatchAlarmRecordDTO> pageQuery);

    Integer countByCondition(@Param("model") BatchAlarmRecordDTO query);

    void deleteByAlarmId(@Param("alarmId") Long alarmId);

    List<Long> listAlarmIds(@Param("ids") List<Long> ids);

    Long insert(BatchAlarmRecord batchJobAlarm);

}
