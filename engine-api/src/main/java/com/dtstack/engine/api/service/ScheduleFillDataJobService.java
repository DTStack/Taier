package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.domain.ScheduleFillDataJob;

import java.util.List;

public interface ScheduleFillDataJobService {
    @Forbidden
    public boolean checkExistsName(String jobName, long projectId);

    @Forbidden
    public List<ScheduleFillDataJob> getFillJobList(List<String> fillJobName, long projectId);

    @Forbidden
    public ScheduleFillDataJob saveData(String jobName, long tenantId, long projectId, String runDay,
                                        String fromDay, String toDay, long userId, Integer appType, Long dtuicTenantId);
}
