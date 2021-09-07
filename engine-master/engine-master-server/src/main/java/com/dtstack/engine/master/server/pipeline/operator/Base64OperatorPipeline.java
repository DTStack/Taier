package com.dtstack.engine.master.server.pipeline.operator;

import com.dtstack.engine.master.server.pipeline.IPipeline;
import com.dtstack.engine.common.util.Base64Util;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class Base64OperatorPipeline extends IPipeline.AbstractPipeline {

    public Base64OperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) {
        String base64String = (String) super.getExecuteValue(actionParam,pipelineParam);
        if (StringUtils.isNotBlank(base64String)) {
            pipelineParam.put(pipelineKey, Base64Util.baseEncode(base64String));
        }
    }
}
