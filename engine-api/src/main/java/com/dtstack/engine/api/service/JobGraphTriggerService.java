package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.domain.JobGraphTrigger;

import java.sql.Timestamp;

public interface JobGraphTriggerService {

    @Forbidden
    public JobGraphTrigger getTriggerByDate(Timestamp timestamp, int triggerType);

    @Forbidden
    public boolean checkHasBuildJobGraph(Timestamp todayTime);

    @Forbidden
    public void addJobTrigger(Timestamp timestamp);
}
