package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;

/**
 * Reason:
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SlotJudge {

    public static boolean judgeSlotsAndAgainExecute(String engineType, String jobId) {

        JobSubmitExecutor executor = JobSubmitExecutor.getInstance();

        if(EngineType.isFlink(engineType)){
            String message = executor.getEngineMessageByHttp(engineType,String.format(FlinkStandaloneRestParseUtil.EXCEPTION_INFO, jobId));
            return FlinkStandaloneRestParseUtil.checkNoSlots(message);
        }
        return false;
    }

}
