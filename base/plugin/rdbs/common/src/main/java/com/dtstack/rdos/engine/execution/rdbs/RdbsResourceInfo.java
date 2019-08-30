package com.dtstack.rdos.engine.execution.rdbs;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.resource.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.rdbs.executor.RdbsExeQueue;

/**
 * Reason:
 * Date: 2018/1/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdbsResourceInfo implements EngineResourceInfo {

    private RdbsExeQueue rdbsExeQueue;

    public RdbsResourceInfo(RdbsExeQueue rdbsExeQueue){
        this.rdbsExeQueue = rdbsExeQueue;
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return rdbsExeQueue.checkCanSubmit();
    }
}
