package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeDao {

    ScheduleTaskTaskShade getOne(@Param("id") long id);

    ScheduleTaskTaskShade getOneByTaskId(@Param("taskId") Long taskId, @Param("parentTaskId") Long parentTaskId);

    List<ScheduleTaskTaskShade> listChildTask(@Param("parentTaskId") long parentTaskId);

    List<ScheduleTaskTaskShade> listParentTask(@Param("childTaskId") long childTaskId);

    Integer deleteByTaskId(@Param("taskId") long taskId,@Param("appType")Integer appType);

    Integer insert(ScheduleTaskTaskShade scheduleTaskTaskShade);

    Integer update(ScheduleTaskTaskShade scheduleTaskTaskShade);


}
