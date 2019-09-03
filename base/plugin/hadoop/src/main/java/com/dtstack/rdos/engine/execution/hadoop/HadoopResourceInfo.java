package com.dtstack.rdos.engine.execution.hadoop;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rods.engine.execution.base.resource.EngineResourceInfo;


public class HadoopResourceInfo implements EngineResourceInfo {

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return true;
    }
}
