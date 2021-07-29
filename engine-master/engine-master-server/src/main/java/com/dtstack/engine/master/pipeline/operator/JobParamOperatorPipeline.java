package com.dtstack.engine.master.pipeline.operator;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.pipeline.IPipeline;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class JobParamOperatorPipeline extends IPipeline.AbstractPipeline {

    public JobParamOperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        String urlKey = (String) super.getExecuteValue(actionParam,pipelineParam);
        if(StringUtils.isNotBlank(urlKey)){
            @SuppressWarnings("unchecked")
            List<ScheduleTaskParamShade> taskParamShades = (List) pipelineParam.get(taskParamsToReplaceKey);
            ScheduleJob scheduleJob = (ScheduleJob) pipelineParam.get(scheduleJobKey);
            if (null == scheduleJob) {
                throw new RdosDefineException("upload param pipeline schedule job can not be null");
            }
            pipelineParam.put(pipelineKey,new JobParamReplace().paramReplace(urlKey,taskParamShades,scheduleJob.getCycTime()));
        }
    }
}
