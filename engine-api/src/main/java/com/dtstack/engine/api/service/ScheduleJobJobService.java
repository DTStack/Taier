package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.sdk.core.common.DtInsightServer;
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
    ScheduleJobVO displayOffSpring( Long jobId,
                                    Long projectId,
                                    Integer level) throws Exception;
    /**
     * 为工作流节点展开子节点
     */
    @RequestLine("POST /node/scheduleJobJob/displayOffSpringWorkFlow")
    ScheduleJobVO displayOffSpringWorkFlow( Long jobId, Integer appType) throws Exception;

    @RequestLine("POST /node/scheduleJobJob/displayForefathers")
    ScheduleJobVO displayForefathers( Long jobId,  Integer level) throws Exception;
}