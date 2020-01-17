package com.dtstack.task.server.impl;

import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.task.common.enums.EScheduleType;
import com.dtstack.task.dao.JobGraphTriggerDao;
import com.dtstack.task.domain.JobGraphTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class JobGraphTriggerService {

    @Autowired
    private JobGraphTriggerDao jobGraphTriggerDao;

    @Forbidden
    public JobGraphTrigger getTriggerByDate(Timestamp timestamp, int triggerType){
        return jobGraphTriggerDao.getByTriggerTimeAndTriggerType(timestamp, triggerType);
    }

    @Forbidden
    public boolean checkHasBuildJobGraph(Timestamp todayTime){
        JobGraphTrigger trigger = getTriggerByDate(todayTime, EScheduleType.NORMAL_SCHEDULE.getType());
        return trigger != null;
    }

    @Forbidden
    public void addJobTrigger(Timestamp timestamp){
        JobGraphTrigger jobGraphTrigger = new JobGraphTrigger();
        jobGraphTrigger.setTriggerTime(timestamp);
        jobGraphTrigger.setTriggerType(0);

        //新增jobTrigger
        jobGraphTriggerDao.insert(jobGraphTrigger);
    }

}
