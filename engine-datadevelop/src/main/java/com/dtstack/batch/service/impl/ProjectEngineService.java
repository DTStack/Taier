package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.ProjectEngineDao;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.engine.domain.ScheduleEngineProject;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.engine.master.impl.ProjectService;
import com.dtstack.engine.master.impl.TenantService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目关联引擎相关
 * Date: 2019/6/3
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
@Slf4j
public class ProjectEngineService {

    @Autowired
    private ProjectEngineDao projectEngineDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TenantService tenantService;

    /**
     * 获取租户已经关联的引擎对应的DB
     *
     * @param dtuicTenantIdList
     * @param engineType
     * @return
     */
    @Forbidden
    public List<String> getTenantUsedDbName(List<Long> dtuicTenantIdList, Integer engineType) {
        if (CollectionUtils.isEmpty(dtuicTenantIdList)) {
            return Lists.newArrayList();
        }
        List<Tenant> tenants = tenantService.listByDtUicTenantIds(dtuicTenantIdList);
        List<Long> tenantIds = tenants.stream().map(Tenant::getId).collect(Collectors.toList());
        List<ScheduleEngineProject> projects = projectService.listByTenantIds(tenantIds);
        List<Long> projectIds = projects.stream().map(ScheduleEngineProject::getProjectId).collect(Collectors.toList());
        List<ProjectEngine> projectEngines = projectEngineDao.listIdentityByProjectIdAndType(projectIds, engineType);
        return projectEngines.stream().map(ProjectEngine::getEngineIdentity).collect(Collectors.toList());
    }

    @Forbidden
    public ProjectEngine getProjectDb(Long projectId, Integer engineType) {
        return projectEngineDao.getByProjectAndEngineType(projectId, engineType);
    }

    @Forbidden
    public ProjectEngine getProjectEngineByTableType(Long projectId, Integer tableType) {
        MultiEngineType multiEngineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType);
        return projectEngineDao.getByProjectAndEngineType(projectId, multiEngineType.getType());
    }

    @Forbidden
    public String getProjectDbByTableType(Long projectId, Integer tableType) {
        ProjectEngine projectEngine = getProjectEngineByTableType(projectId, tableType);
        return projectEngine.getEngineIdentity();
    }

    @Forbidden
    public List<Integer> getUsedEngineTypeList(Long projectId) {
        return projectEngineDao.getUsedEngineTypeList(projectId);
    }

    /**
     * 获取projectId 关联的所有引擎信息
     *
     * @param projectIds
     * @return
     */
    @Forbidden
    public Table<Long, Integer, ProjectEngine> getProjectEngineMap(Collection<Long> projectIds) {

        List<ProjectEngine> projectEngines = projectEngineDao.listByProjectIds(projectIds);

        Table<Long, Integer, ProjectEngine> table = HashBasedTable.create();
        projectEngines.forEach(pe -> table.put(pe.getProjectId(), pe.getEngineType(), pe));

        return table;
    }

    public void deleteByProjectId(Long projectId, Long userId) {
        projectEngineDao.deleteByProjectId(projectId, userId);
    }

    @Forbidden
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(ProjectEngine projectEngine) {
        return projectEngineDao.insert(projectEngine);
    }

    @Forbidden
    public ScheduleEngineProject getProjectByDbName(String dbName, Integer engineType, Long tenantId) {
        ProjectEngine projectEngine = projectEngineDao.getByIdentityAndEngineTypeAndTenantId(dbName, engineType, tenantId);
        if (projectEngine == null) {
            log.error("ProjectEngine not exist, by dbName : {}, engineType : {}, tenantId : {}", dbName, engineType, tenantId);
            throw new RdosDefineException(ErrorCode.PROJECT_ENGINE_NOT_EXIST);
        }
        return projectService.getProjectById(projectEngine.getProjectId());
    }

}