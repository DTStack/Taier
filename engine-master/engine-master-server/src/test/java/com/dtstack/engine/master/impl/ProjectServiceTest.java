package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.schedule.common.enums.EProjectScheduleStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2020-10-30
 */
public class ProjectServiceTest extends AbstractTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Test
    public void testUpdateProjectSchedule(){
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getScheduleTaskShade();
        Long projectId = scheduleTaskShade.getProjectId();
        projectService.updateSchedule(projectId,scheduleTaskShade.getAppType(), EProjectScheduleStatus.PAUSE.getStatus());
        ScheduleTaskShade dbScheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(scheduleTaskShade.getTaskId(), scheduleTaskShade.getAppType());
        Assert.assertEquals(dbScheduleTaskShade.getProjectScheduleStatus(),EProjectScheduleStatus.PAUSE.getStatus());
    }
}
