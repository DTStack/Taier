package com.dtstack.task.server.impl;

import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.task.dao.BatchJobAlarmDao;
import com.dtstack.task.domain.BatchJob;
import com.dtstack.task.domain.BatchJobAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class BatchJobAlarmService {

    @Autowired
    private BatchJobAlarmDao batchJobAlarmDao;

    @Forbidden
    public BatchJobAlarm saveBatchJobAlarm(BatchJob batchJob){

        Integer countNum = batchJobAlarmDao.countByJobId(batchJob.getId());
        if(countNum > 0){
            return null;
        }

        BatchJobAlarm batchJobAlarm = new BatchJobAlarm();
        batchJobAlarm.setTenantId(batchJob.getTenantId());
        batchJobAlarm.setProjectId(batchJob.getProjectId());
        batchJobAlarm.setDtuicTenantId(batchJob.getDtuicTenantId());
        batchJobAlarm.setAppType(batchJob.getAppType());
        batchJobAlarm.setJobId(batchJob.getId());
        batchJobAlarm.setTaskStatus(batchJob.getStatus());
        batchJobAlarm.setTaskId(batchJob.getTaskId());

        batchJobAlarmDao.insert(batchJobAlarm);
        return  batchJobAlarm;
    }
}
