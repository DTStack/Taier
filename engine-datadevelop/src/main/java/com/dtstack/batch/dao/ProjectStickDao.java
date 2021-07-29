package com.dtstack.batch.dao;

import com.dtstack.batch.domain.ProjectStick;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sanyue
 */
public interface ProjectStickDao {

    Integer updateStick(ProjectStick projectStick);

    Integer insert(ProjectStick projectStick);

    ProjectStick getByUserIdAndProjectId(@Param("createUserId") Long createUserId, @Param("tenantId") Long tenantId, @Param("projectId") Long appointProjectId);

    List<ProjectStick> listByProjectIdsAndUserId(@Param("projectIds") List<Long> usefulProjectIds, @Param("createUserId") Long createUserId, @Param("tenantId") Long tenantId);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("modifyUserId") Long modifyUserId);
}
