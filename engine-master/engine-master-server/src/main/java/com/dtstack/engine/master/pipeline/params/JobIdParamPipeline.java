package com.dtstack.engine.master.pipeline.params;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.pipeline.IPipeline;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class JobIdParamPipeline extends IPipeline.AbstractPipeline {

    public JobIdParamPipeline() {
        super("jobId");
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) {
        ScheduleJob scheduleJob = (ScheduleJob) pipelineParam.get(scheduleJobKey);
        if (null == scheduleJob) {
            throw new RdosDefineException("jobId param pipeline schedule job can not be null");
        }
        pipelineParam.put(pipelineKey, scheduleJob.getJobId());
    }
}
