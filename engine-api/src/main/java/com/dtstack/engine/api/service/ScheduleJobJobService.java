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
    public ScheduleJobVO displayOffSpring(@Param("jobId") Long jobId,
                                          @Param("projectId") Long projectId,
                                          @Param("level") Integer level) throws Exception;
    /**
     * 为工作流节点展开子节点
     */
    public ScheduleJobVO displayOffSpringWorkFlow(@Param("jobId") Long jobId, @Param("appType")Integer appType) throws Exception;

    public ScheduleJobVO displayForefathers(@Param("jobId") Long jobId, @Param("level") Integer level) throws Exception;
}