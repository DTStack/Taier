/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.master.controller.param.ScheduleEngineProjectParam;
import com.dtstack.engine.master.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.master.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.master.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.domain.ScheduleEngineProject;
import com.dtstack.engine.common.enums.AppType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-01-19
 */
@Service
public class ProjectService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private EnvironmentContext environmentContext;


    public void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus) {
        if (null == projectId || null == appType || null == scheduleStatus) {
            return;
        }
        LOGGER.info("update project {} status {} ",projectId,scheduleStatus);
        scheduleTaskShadeDao.updateProjectScheduleStatus(projectId,appType,scheduleStatus);
    }

    public void addProjectOrUpdate(ScheduleEngineProjectParam scheduleEngineProjectParam) {
        Long projectId = scheduleEngineProjectParam.getProjectId();
        Integer appType = scheduleEngineProjectParam.getAppType();

        if (projectId == null || appType == null) {
            throw new RdosDefineException("projectId or appType can not be empty");
        }

        ScheduleEngineProject scheduleEngineProject = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(projectId,appType);


        ScheduleEngineProject project = buildEngineProject(scheduleEngineProjectParam);
        if (scheduleEngineProject == null) {
            // 插入项目
            scheduleEngineProjectDao.insert(project);
        } else {
            // 更新项目
            project.setId(scheduleEngineProject.getId());
            scheduleEngineProjectDao.updateById(project);
        }
    }


    private ScheduleEngineProject buildEngineProject(ScheduleEngineProjectParam scheduleEngineProjectParam) {
        ScheduleEngineProject scheduleEngineProject = new ScheduleEngineProject();
        if (scheduleEngineProjectParam.getId() != null) {
            scheduleEngineProject.setId(scheduleEngineProjectParam.getId());
        }

        if (scheduleEngineProjectParam.getProjectId() != null) {
            scheduleEngineProject.setProjectId(scheduleEngineProjectParam.getProjectId());
        }

        if (scheduleEngineProjectParam.getUicTenantId() != null) {
            scheduleEngineProject.setUicTenantId(scheduleEngineProjectParam.getUicTenantId());
        }

        if (scheduleEngineProjectParam.getAppType() != null) {
            scheduleEngineProject.setAppType(scheduleEngineProjectParam.getAppType());
        }

        if (scheduleEngineProjectParam.getProjectName() != null) {
            scheduleEngineProject.setProjectName(scheduleEngineProjectParam.getProjectName());
        }

        if (scheduleEngineProjectParam.getProjectAlias() != null) {
            scheduleEngineProject.setProjectAlias(scheduleEngineProjectParam.getProjectAlias());
        }

        if (scheduleEngineProjectParam.getProjectIdentifier() != null) {
            scheduleEngineProject.setProjectIdentifier(scheduleEngineProjectParam.getProjectIdentifier());
        }

        if (scheduleEngineProjectParam.getProjectDesc() != null) {
            scheduleEngineProject.setProjectDesc(scheduleEngineProjectParam.getProjectDesc());
        }

        if (scheduleEngineProjectParam.getStatus() != null) {
            scheduleEngineProject.setStatus(scheduleEngineProjectParam.getStatus());
        }

        if (scheduleEngineProjectParam.getCreateUserId() != null) {
            scheduleEngineProject.setCreateUserId(scheduleEngineProjectParam.getCreateUserId());
        }

        if (scheduleEngineProjectParam.getIsDeleted() != null) {
            scheduleEngineProject.setIsDeleted(scheduleEngineProjectParam.getIsDeleted());
        }

        scheduleEngineProject.setGmtModified(new Date());
        return scheduleEngineProject;
    }

    public void deleteProject(Long projectId, Integer appType) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (projectId == null) {
            throw new RdosDefineException("projectId must be passed");
        }

        scheduleEngineProjectDao.deleteByProjectIdAppType(projectId, appType);
    }

    public List<ScheduleEngineProjectVO> findFuzzyProjectByProjectAlias(String name, Integer appType, Long uicTenantId,Long projectId) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (uicTenantId == null) {
            throw new RdosDefineException("uicTenantId must be passed");
        }

        List<ScheduleEngineProject> deans = scheduleEngineProjectDao.selectFuzzyProjectByProjectAlias(name, appType, uicTenantId,projectId, environmentContext.getFuzzyProjectByProjectAliasLimit());

        return buildProjectList(deans);
    }

    private List<ScheduleEngineProjectVO> buildProjectList(List<ScheduleEngineProject> deans) {
        if (CollectionUtils.isEmpty(deans)) {
            return Lists.newArrayList();
        }

        List<ScheduleEngineProjectVO> vos = Lists.newArrayList();

        for (ScheduleEngineProject dean : deans) {
            ScheduleEngineProjectVO vo = new ScheduleEngineProjectVO();
            build(dean, vo);

            vos.add(vo);
        }

        return vos;
    }

    private void build(ScheduleEngineProject dean, ScheduleEngineProjectVO vo) {
        vo.setId(dean.getId());
        vo.setProjectId(dean.getProjectId());
        vo.setUicTenantId(dean.getUicTenantId());
        vo.setAppType(dean.getAppType());
        vo.setProjectName(dean.getProjectName());
        vo.setProjectAlias(dean.getProjectAlias());
        vo.setProjectIdentifier(dean.getProjectIdentifier());
        vo.setProjectDesc(dean.getProjectDesc());
        vo.setStatus(dean.getStatus());
        vo.setCreateUserId(dean.getCreateUserId());
        vo.setGmtCreate(dean.getGmtCreate());
        vo.setGmtModified(dean.getGmtModified());
        vo.setIsDeleted(dean.getIsDeleted());
    }

    public ScheduleEngineProjectVO findProject(Long projectId, Integer appType) {
        if (projectId == null) {
            throw new RdosDefineException("projectId not null");
        }

        if (appType == null) {
            throw new RdosDefineException("projectId not null");
        }

        ScheduleEngineProject dean = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(projectId,appType);
        ScheduleEngineProjectVO vo = new ScheduleEngineProjectVO();

        if (dean == null) {
            return null;
        }
        
        build(dean, vo);
        return vo;
    }

    public ScheduleEngineProject getProjectById(Long projectId){
        if (projectId == null) {
            throw new RdosDefineException("projectId not null");
        }
        ScheduleEngineProject project = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(projectId, AppType.RDOS.getType());
        if (project == null) {
            LOGGER.error("Project not exist, by projectId : {}", projectId);
            throw new RdosDefineException("Project not exist");
        }
        return project;
    }

    public List<NotDeleteProjectVO> getNotDeleteTaskByProjectId(Long projectId, Integer appType) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (projectId == null) {
            throw new RdosDefineException("projectId must be passed");
        }

        List<NotDeleteProjectVO> notDeleteTaskVOS = Lists.newArrayList();

        List<ScheduleTaskTaskShade> scheduleTaskShades = scheduleTaskShadeService.getTaskOtherPlatformByProjectId(projectId, appType, environmentContext.getListChildTaskLimit());

        for (ScheduleTaskTaskShade scheduleTaskShade : scheduleTaskShades) {
            ScheduleTaskShade batchTaskById = scheduleTaskShadeService.getBatchTaskById(scheduleTaskShade.getParentTaskId(), scheduleTaskShade.getParentAppType());
            List<NotDeleteTaskVO> notDeleteTask = scheduleTaskShadeService.getNotDeleteTask(scheduleTaskShade.getParentTaskId(), scheduleTaskShade.getParentAppType());
            NotDeleteProjectVO notDeleteProjectVO = new NotDeleteProjectVO();

            notDeleteProjectVO.setTaskName(batchTaskById.getName());
            notDeleteProjectVO.setNotDeleteTaskVOList(notDeleteTask);
            notDeleteTaskVOS.add(notDeleteProjectVO);
        }

        return notDeleteTaskVOS;
    }

    public ScheduleEngineProject getByName(String projectName, Long dtUicTenantId) {
        ScheduleEngineProject pj = scheduleEngineProjectDao.getByName(projectName, dtUicTenantId);
        if (pj == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        return pj;
    }

    /**
     * 根据租户id列表查询项目
     * @param tenantIds
     * @return
     */
    public List<ScheduleEngineProject> listByTenantIds(List<Long> tenantIds) {
        return scheduleEngineProjectDao.listByTenantIds(tenantIds);
    }
}

