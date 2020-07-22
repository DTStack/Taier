package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.vo.ScheduleJobVO;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobJobService {
    /**
     * @author toutian
     */
    public ScheduleJobVO displayOffSpring( Long jobId,
                                           Long projectId,
                                           Integer level) throws Exception;
    /**
     * 为工作流节点展开子节点
     */
    public ScheduleJobVO displayOffSpringWorkFlow( Long jobId, Integer appType) throws Exception;

    public ScheduleJobVO displayForefathers( Long jobId,  Integer level) throws Exception;
}