package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
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

    }
}
