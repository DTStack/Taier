package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yuebai
 * @date 2020-01-19
 */
@Service
public class ProjectService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    public void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus) {
        if (null == projectId || null == appType || null == scheduleStatus) {
            return;
        }
        LOGGER.info("update project {} status {} ",projectId,scheduleStatus);
        scheduleTaskShadeDao.updateProjectScheduleStatus(projectId,appType,scheduleStatus);
    }
}

