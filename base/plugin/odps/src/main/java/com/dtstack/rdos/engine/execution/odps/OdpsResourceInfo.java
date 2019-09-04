package com.dtstack.rdos.engine.execution.odps;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rods.engine.execution.base.resource.EngineResourceInfo;

/**
 * Reason:
 * Date: 2018/2/12
 * Company: www.dtstack.com
 * @author jingzhen
 */

public class OdpsResourceInfo implements EngineResourceInfo {

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return true;
    }
}
