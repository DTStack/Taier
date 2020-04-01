package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.vo.BatchJobVO;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobJobService {
    /**
     * @author toutian
     */
    public BatchJobVO displayOffSpring(@Param("jobId") Long jobId,
                                       @Param("projectId") Long projectId,
                                       @Param("level") Integer level) throws Exception;
    /**
     * 为工作流节点展开子节点
     */
    public BatchJobVO displayOffSpringWorkFlow(@Param("jobId") Long jobId,@Param("appType")Integer appType) throws Exception;

    public BatchJobVO displayForefathers(@Param("jobId") Long jobId, @Param("level") Integer level) throws Exception;
}