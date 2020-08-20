package com.dtstack.engine.odps;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JudgeResult;

/**
 * Reason:
 * Date: 2018/2/12
 * Company: www.dtstack.com
 * @author jingzhen
 */

public class OdpsResourceInfo implements EngineResourceInfo {

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.newInstance(true, "");
    }
}
