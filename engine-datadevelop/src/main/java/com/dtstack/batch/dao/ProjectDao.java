package com.dtstack.batch.dao;

import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.po.ProjectCataloguePO;
import com.dtstack.batch.dto.ProjectDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sishu.yss
 */
public interface ProjectDao {

    List<Project> listByIds(@Param("projectIds") Collection<Long> projectIds);

    Integer updateStatusById(@Param("id") long id, @Param("status") int status, @Param("modifyUserId") Long modifyUserId);

    Project getOne(@Param("projectId") long projectId);

    /**
     * 根据id查询项目 忽略已删除标示位置
     *
     * @param projectIds
     * @return
     */
    List<Project> getOneWithoutDeleted(@Param("projectIds") List<Long> projectIds);

    Project getByProjectIdentifier(@Param("projectIdentifier") String projectIdentifier);

    Long getProjectIdByProjectIdentifier(@Param("projectIdentifier") String projectIdentifier);

    List<Project> listByProjectAlias(@Param("projectAlias") String projectAlias);

    Integer deleteById(@Param("projectId") Long projectId, @Param("modifyUserId") Long modifyUserId);

    Project getByName(@Param("projectName") String projectName, @Param("tenantId") Long tenantId);

    List<Project> listByIdsAndTenantId(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId);

    /**
     * 根据ids和projectName 模糊查询 list
     * @param projectIds
     * @param projectName
     * @return
     */
    List<Project> getListByIdsAndProjectName(@Param("projectIds") List<Long> projectIds, @Param("projectName") String projectName);

    List<Project> listByIdNotIn(@Param("projectIds") List<Long> projectIds, @Param("tenantId") Long tenantId, @Param("pageQuery") PageQuery pageQuery);

    Integer update(Project project);

    Integer insert(Project project);

    List<Project> listByIdsAndProjectAlias(@Param("projectIds") Collection<Long> projectIds, @Param("projectAlias") String projectAlias, @Param("pageQuery") PageQuery pageQuery);

    Integer countByIdsAndProjectAlias(@Param("projectIds") List<Long> projectIds, @Param("projectAlias") String projectAlias);

    List<ProjectDTO> listJobSumByIdsAndFuzzyNameAndType(@Param("ids") List<Long> ids, @Param("fuzzyName") String fuzzyName, @Param("projectType") Integer projectType, @Param("userId") Long userId,
                                                        @Param("model") PageQuery pageQuery, @Param("tenantId") Long tenantId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("statuses") List<Integer> statuses, @Param("type")Integer type, @Param("catalogueId") Long catalogueId);

    Integer countByIdsAndFuzzyNameAndType(@Param("ids") List<Long> ids, @Param("fuzzyName") String fuzzyName, @Param("projectType") Integer projectType, @Param("jobStatus") List<Integer> jobStatus, @Param("model") PageQuery pageQuery, @Param("tenantId") Long tenantId, @Param("catalogueId") Long catalogueId);

    List<Map<String, Object>> mapProjectNames(@Param("tenantId") Long tenantId);

    List<Project> listByType(@Param("tenantId") Long tenantId, @Param("name") String name, @Param("projectType") Integer projectType);

    Project getByProduceProjectId(@Param("produceProjectId") Long produceProjectId);

    Integer updateProduceProject(@Param("projectId") Long projectId, @Param("produceProjectId") Long produceProjectId, @Param("projectType") Integer projectType, @Param("modifyUserId") Long modifyUserId);

    Integer updateScheduleById(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("scheduleStatus") Integer scheduleStatus, @Param("modifyUserId") Long modifyUserId);

    List<Long> listIdByScheduleStatus(@Param("scheduleStatus") Integer scheduleStatus);

    List<String> listNameByTenantId(@Param("tenantId") Long tenantId);

    List<Long> listIdByTenantId(@Param("tenantId") Long tenantId);

    List<Long> listIdByTenantIdForMask(@Param("tenantId") Long tenantId);

    List<Long> listAllForMask();

    Integer updateAllowDownload(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("isAllowDownload") Integer isAllowDownload, @Param("modifyUserId") Long modifyUserId);

    List<Project> listByTenantId(@Param("tenantId") Long tenantId);

    List<ProjectCataloguePO> getCatalogueListByTenantIdAndCatalogueId(@Param("projectIds") Set<Long> projectIds, @Param("tenantId") Long tenantId, @Param("catalogueType") Integer catalogueType);

    List<Project> listAll();

    /**
     * 根据项目标识获取对应的TenantId
     * @param ProjectIdentifier 项目标识
     * @return 创建租户list
     */
    List<Long> getTenantIdListByProjectIdentifier(@Param("projectIdentifier") String ProjectIdentifier);

    /**
     * 根据目录获取下面所有的项目
     *
     * @param catalogueId
     * @return
     */
    List<Project> getByCatalogueId(@Param("catalogueId") Long catalogueId);

    List<Project> listByTenantIds(@Param("tenantIds") List<Long> tenantIds);
}
