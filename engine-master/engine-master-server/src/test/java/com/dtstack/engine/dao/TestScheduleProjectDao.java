package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleEngineProject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2021-07-26
 */
public interface TestScheduleProjectDao {


    @Insert({" insert into schedule_engine_project ( `project_id`, `uic_tenant_id`," +
            "        `app_type`, `project_name`, `project_alias`," +
            "        `project_Identifier`, `project_desc`, `status`," +
            "        `create_user_id`, `gmt_modified`,`white_status`) VALUES (#{project.projectId},#{project.uicTenantId},#{project.appType},#{project.projectName}," +
            "#{project.projectAlias},#{project.projectIdentifier},#{project.projectDesc},#{project.status}," +
            "#{project.createUserId},now(),#{project.whiteStatus}" +
            ")"})
    @Options()
    Integer insert(@Param("project") ScheduleEngineProject project);
}
