package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleEngineProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/5 10:47 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleEngineProjectDao {

    Integer insert(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    ScheduleEngineProject getProjectById(@Param("id") Long id);

    Integer updateById(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    Integer deleteByProjectIdAppType(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    List<ScheduleEngineProject> selectFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId, @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit);
}
