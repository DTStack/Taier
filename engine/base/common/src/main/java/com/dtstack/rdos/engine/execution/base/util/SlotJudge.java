package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.engine.execution.base.ClientOperator;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import org.apache.commons.lang3.StringUtils;

/**
 * FIXME
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SlotJudge {

    public final static String FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION = "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job";

    public final static String FLINK_EXCEPTION_URL = "/jobs/%s/exceptions";

    public static boolean judgeSlotsAndAgainExecute(String engineType, String jobId) {

        if(EngineType.isFlink(engineType)){
            String message = ClientOperator.getInstance().getEngineMessageByHttp(engineType,String.format(FLINK_EXCEPTION_URL, jobId));
            return checkFlinkNoSlots(message);
        }

        return false;
    }

    public static boolean checkFlinkNoSlots(String msg){
        if(StringUtils.isNotBlank(msg) && msg.contains(FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION)){
            return true;
        }
        return false;
    }

}
