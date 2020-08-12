package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobJobService extends DtInsightServer {
    /**
     * @author toutian
     */
    @RequestLine("POST /node/scheduleJobJob/displayOffSpring")
    ApiResponse<ScheduleJobVO> displayOffSpring(@Param("jobId") Long jobId,
                                                @Param("projectId") Long projectId,
                                                @Param("level") Integer level) throws Exception;
    /**
     * 为工作流节点展开子节点
     */
    @RequestLine("POST /node/scheduleJobJob/displayOffSpringWorkFlow")
    ApiResponse<ScheduleJobVO> displayOffSpringWorkFlow(@Param("jobId") Long jobId, @Param("appType") Integer appType) throws Exception;

    @RequestLine("POST /node/scheduleJobJob/displayForefathers")
    ApiResponse<ScheduleJobVO> displayForefathers(@Param("jobId") Long jobId, @Param("level") Integer level) throws Exception;
}