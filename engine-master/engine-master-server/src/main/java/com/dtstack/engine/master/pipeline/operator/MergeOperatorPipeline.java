package com.dtstack.engine.master.pipeline.operator;

import com.dtstack.engine.master.pipeline.IPipeline;
import com.dtstack.engine.master.pipeline.params.UploadParamPipeline;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class MergeOperatorPipeline extends IPipeline.AbstractPipeline {

    public MergeOperatorPipeline() {
        super(null);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        //1 移除无用参数
        pipelineParam.remove(scheduleJobKey);
        pipelineParam.remove(taskShadeKey);
        pipelineParam.remove(taskParamsToReplaceKey);
        pipelineParam.remove(UploadParamPipeline.pluginInfoKey);
        pipelineParam.remove(UploadParamPipeline.workOperatorKey);
        pipelineParam.remove(UploadParamPipeline.fileUploadPathKey);
        //2 处理后的值替换提交参数值
        actionParam.putAll(pipelineParam);
    }
}
