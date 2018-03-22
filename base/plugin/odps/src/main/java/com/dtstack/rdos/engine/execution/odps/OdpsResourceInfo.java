package com.dtstack.rdos.engine.execution.odps;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

/**
 * Reason:
 * Date: 2018/2/12
 * Company: www.dtstack.com
 * @author jingzhen
 */

public class OdpsResourceInfo extends EngineResourceInfo {

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return true;
    }
}
