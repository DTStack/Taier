package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;

public interface ProjectService {
    public void updateSchedule(@Param("projectId")Long projectId, @Param("appType")Integer appType, @Param("scheduleStatus")Integer scheduleStatus);
}
