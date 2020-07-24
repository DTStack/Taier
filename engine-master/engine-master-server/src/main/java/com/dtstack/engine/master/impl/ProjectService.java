package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author yuebai
 * @date 2020-01-19
 */
@Service
public class ProjectService {

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    public void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus) {
        if (Objects.isNull(projectId) || Objects.isNull(appType) || Objects.isNull(scheduleStatus)) {
            return;
        }
        logger.info("update project {} status {} ",projectId,scheduleStatus);
        scheduleTaskShadeDao.updateProjectScheduleStatus(projectId,appType,scheduleStatus);
    }
}

