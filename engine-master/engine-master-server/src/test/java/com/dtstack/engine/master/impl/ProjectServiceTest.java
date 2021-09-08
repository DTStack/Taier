package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.ProjectStatus;
import com.dtstack.engine.domain.ScheduleEngineProject;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dao.TestScheduleProjectDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.utils.ValueUtils;
import com.dtstack.engine.common.enums.AppType;
import com.dtstack.engine.common.enums.EProjectScheduleStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-10-30
 */
public class ProjectServiceTest extends AbstractTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private TestScheduleProjectDao scheduleProjectDao;

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateProjectSchedule(){
        ScheduleTaskShade scheduleTaskShade =  Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setTaskId(ValueUtils.getChangedLong());
        scheduleTaskShadeDao.insert(scheduleTaskShade);
        Long projectId = scheduleTaskShade.getProjectId();
        projectService.updateSchedule(projectId,scheduleTaskShade.getAppType(), EProjectScheduleStatus.PAUSE.getStatus());
        ScheduleTaskShade dbScheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(scheduleTaskShade.getTaskId(), scheduleTaskShade.getAppType());
        Assert.assertEquals(dbScheduleTaskShade.getProjectScheduleStatus(),EProjectScheduleStatus.PAUSE.getStatus());
    }


    @Test
    public void testWhiteList(){
        ScheduleEngineProject project = new ScheduleEngineProject();
        project.setAppType(AppType.RDOS.getType());
        project.setProjectId(100L);
        project.setCreateUserId(100);
        project.setProjectName("testWhiteList");
        project.setProjectIdentifier("");
        project.setProjectAlias("");
        project.setUicTenantId(-1L);
        project.setStatus(ProjectStatus.NORMAL.getStatus());
        project.setWhiteStatus(1);
        scheduleProjectDao.insert(project);
        ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShadeTemplate.setAppType(project.getAppType());
        scheduleTaskShadeTemplate.setProjectId(project.getProjectId());
        scheduleTaskShadeDao.insert(scheduleTaskShadeTemplate);
        List<ScheduleEngineProject> scheduleEngineProjects = scheduleEngineProjectDao.listWhiteListProject();
        Assert.assertNotNull(scheduleEngineProjects);

    }
}
