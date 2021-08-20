package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.utils.ValueUtils;
import com.dtstack.schedule.common.enums.EProjectScheduleStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
}
