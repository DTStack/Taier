package com.dtstack.engine.master.pipeline.operator;

import com.dtstack.engine.master.pipeline.IPipeline;
import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;

import java.net.URLEncoder;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class URLEncoderOperatorPipeline extends IPipeline.AbstractPipeline {

    public URLEncoderOperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        String urlKey = (String) super.getExecuteValue(actionParam,pipelineParam);
        if(StringUtils.isNotBlank(urlKey)){
            pipelineParam.put(pipelineKey,URLEncoder.encode(urlKey, Charsets.UTF_8.name()));
        }
    }
}
