package com.dtstack.engine.rdbs.common;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.rdbs.common.executor.RdbsExeQueue;

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
    public JudgeResult judgeSlots(JobClient jobClient) {
        Boolean rs = rdbsExeQueue.checkCanSubmit();
        if (rs) {
            return JudgeResult.ok();
        }
        return JudgeResult.notOk(rs, "The execution queue is full");
    }
}
