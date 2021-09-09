package com.dtstack.engine.hadoop;

import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.pluginapi.pojo.JudgeResult;


public class HadoopResourceInfo implements EngineResourceInfo {

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.ok();
    }
}
