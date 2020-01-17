package com.dtstack.task.dao;

import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.task.domain.BatchAlarm;
import com.dtstack.task.dto.BatchAlarmDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchAlarmDao {

    List<Long> getAllNeedMonitorTaskId(@Param("projectId") Long projectId);

    List<BatchAlarm> getAllNeedMonitorAlarm(@Param("projectId") Long projectId);

    BatchAlarm getOne(@Param("id") long id);

    BatchAlarm getByNameAndProjectId(@Param("name") String name, @Param("projectId") long projectId);

    List<BatchAlarm> generalQuery(PageQuery<BatchAlarmDTO> pageQuery);

    Integer generalCount(@Param("model") BatchAlarmDTO alarmDTO);

    Integer update(BatchAlarm batchAlarm);

    Integer insert(BatchAlarm batchAlarm);

    List<BatchAlarm> listByTaskId(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);

    Integer deleteAlarmByTask(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);
}
