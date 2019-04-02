package com.dtstack.rdos.engine.execution.flink130;

import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestartStrategy extends IRestartStrategy {

    private final static String FLINK_ENGINE_DOWN = "Could not connect to the leading JobManager";

    private final static String FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION = "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job";


    @Override
    public boolean checkFailureForEngineDown(String msg) {
        if(StringUtils.isNotBlank(msg) && msg.contains(FLINK_ENGINE_DOWN)){
            return true;
        }

        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        if(StringUtils.isNotBlank(msg) && msg.contains(FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION)){
            return true;
        }
        return false;
    }
}
