package com.dtstack.engine.dtscript.execution.hadoop;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.EngineResourceInfo;


public class HadoopResourceInfo implements EngineResourceInfo {

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return true;
    }
}
