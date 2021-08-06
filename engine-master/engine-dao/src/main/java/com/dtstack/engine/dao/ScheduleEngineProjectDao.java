package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleEngineProject;
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

    ScheduleEngineProject getProjectByProjectIdAndApptype(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    Integer updateById(@Param("scheduleEngineProject") ScheduleEngineProject scheduleEngineProject);

    Integer deleteByProjectIdAppType(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    List<ScheduleEngineProject> selectFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId,@Param("projectId") Long projectId, @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit);

    List<ScheduleEngineProject> listByProjectIds(@Param("projectIds") List<Long> projectIds, @Param("appType") Integer appType);
}
