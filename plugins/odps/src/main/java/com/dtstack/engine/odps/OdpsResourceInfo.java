package com.dtstack.engine.odps;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.EngineResourceInfo;

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
