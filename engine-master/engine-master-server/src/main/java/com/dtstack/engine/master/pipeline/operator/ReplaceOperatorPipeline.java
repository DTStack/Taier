package com.dtstack.engine.master.pipeline.operator;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.pipeline.IPipeline;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class ReplaceOperatorPipeline extends IPipeline.AbstractPipeline {

    public ReplaceOperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        String replaceString = (String) super.getExecuteValue(actionParam, pipelineParam);
        if (StringUtils.isBlank(replaceString)) {
            throw new RdosDefineException(String.format("replace operator key %s is null", pipelineKey));
        }
        for (String paramKey : pipelineParam.keySet()) {
            Object paramValue = pipelineParam.get(paramKey);
            if (paramValue instanceof String) {
                replaceString = replaceString.replace(String.format("${%s}", paramKey), (String) paramValue);
            }
        }
        pipelineParam.put(pipelineKey, replaceString);
    }
}
