package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.BatchFillDataJobDao;
import com.dtstack.engine.domain.BatchFillDataJob;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class BatchFillDataJobService {

    @Autowired
    private BatchFillDataJobDao batchFillDataJobDao;

    public boolean checkExistsName(String jobName, long projectId) {
        BatchFillDataJob batchFillDataJob = batchFillDataJobDao.getByJobName(jobName, projectId);
        return batchFillDataJob != null;
    }

    public List<BatchFillDataJob> getFillJobList(List<String> fillJobName, long projectId){
        if(CollectionUtils.isEmpty(fillJobName)){
            return Lists.newArrayList();
        }

        return batchFillDataJobDao.listFillJob(fillJobName, projectId);
    }

    @Transactional
    public BatchFillDataJob saveData(String jobName, long tenantId, long projectId, String runDay,
                                     String fromDay, String toDay, long userId,Integer appType,Long dtuicTenantId) {

        Timestamp currTimeStamp = Timestamp.valueOf(LocalDateTime.now());

        BatchFillDataJob fillDataJob = new BatchFillDataJob();
        fillDataJob.setJobName(jobName);
        fillDataJob.setFromDay(fromDay);
        fillDataJob.setToDay(toDay);
        fillDataJob.setRunDay(runDay);
        fillDataJob.setTenantId(tenantId);
        fillDataJob.setProjectId(projectId);
        fillDataJob.setCreateUserId(userId);
        fillDataJob.setGmtModified(currTimeStamp);
        fillDataJob.setGmtCreate(currTimeStamp);
        fillDataJob.setAppType(appType);
        fillDataJob.setDtuicTenantId(dtuicTenantId);
        batchFillDataJobDao.insert(fillDataJob);
        return fillDataJob;
    }

}
