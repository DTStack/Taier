package com.dtstack.batch.service.job.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.service.ScheduleJobJobService;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchJobJobService {

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private BatchJobService batchJobService;


    /**
     * @author toutian
     */
    public ScheduleJobVO displayOffSpring(Long jobId, Long projectId, Integer level) {
        ScheduleJobVO jobVO = scheduleJobJobService.displayOffSpring(jobId, projectId, level).getData();
        batchJobService.fillProjectAndUserInfo(Lists.newArrayList(jobVO));
        return jobVO;
    }

    /**
     * 为工作流节点展开子节点
     *
     * @param jobId
     * @return
     */
    public ScheduleJobVO displayOffSpringWorkFlow(Long jobId) {
        return scheduleJobJobService.displayOffSpringWorkFlow(jobId, AppType.RDOS.getType()).getData();
    }

    /**
     * @author toutian
     */
    public ScheduleJobVO displayForefathers(Long jobId, Integer level) {
        return scheduleJobJobService.displayForefathers(jobId, level).getData();
    }

}