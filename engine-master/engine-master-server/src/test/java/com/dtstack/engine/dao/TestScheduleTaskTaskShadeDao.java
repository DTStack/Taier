package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleTaskTaskShade;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/21
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestScheduleTaskTaskShadeDao {

	@Insert({"INSERT INTO `schedule_task_task_shade`( `tenant_id`, `project_id`, `dtuic_tenant_id`, `app_type`, `task_id`, `parent_task_id`, `gmt_create`, `gmt_modified`, `is_deleted`) " +
			"VALUES (#{scheduleTaskTaskShade.tenantId}, #{scheduleTaskTaskShade.projectId}, #{scheduleTaskTaskShade.dtuicTenantId}, " +
			"#{scheduleTaskTaskShade.appType}, #{scheduleTaskTaskShade.taskId}, #{scheduleTaskTaskShade.parentTaskId}, " +
			"#{scheduleTaskTaskShade.gmtCreate}, #{scheduleTaskTaskShade.gmtModified}, #{scheduleTaskTaskShade.isDeleted})"})
	@Options(useGeneratedKeys=true, keyProperty = "scheduleTaskTaskShade.id", keyColumn = "id")
	void insert(@Param("scheduleTaskTaskShade") ScheduleTaskTaskShade scheduleTaskTaskShade);
}
