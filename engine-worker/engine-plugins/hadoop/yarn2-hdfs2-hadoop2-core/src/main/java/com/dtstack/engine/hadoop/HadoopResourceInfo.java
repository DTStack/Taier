package com.dtstack.engine.hadoop;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JudgeResult;


public class HadoopResourceInfo implements EngineResourceInfo {

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.newInstance(true, "");
    }
}
