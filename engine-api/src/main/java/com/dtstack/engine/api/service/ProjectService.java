package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;

public interface ProjectService {
    public void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus);
}