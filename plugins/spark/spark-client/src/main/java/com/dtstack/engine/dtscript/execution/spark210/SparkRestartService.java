package com.dtstack.engine.dtscript.execution.spark210;

import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.restart.ARestartService;
import com.dtstack.engine.common.restart.IJobRestartStrategy;
import com.dtstack.engine.dtscript.execution.spark210.restart.SparkUndoRestart;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkRestartService extends ARestartService {

    private final static String SPARK_ENGINE_DOWN = "Current state is not alive: STANDBY";

    @Override
    public boolean checkFailureForEngineDown(String msg) {
        if(msg != null && msg.contains(SPARK_ENGINE_DOWN)){
            return true;
        }

        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        return false;
    }

    @Override
    public IJobRestartStrategy getAndParseErrorLog(String jobId, String engineJobId, String appId, IClient client) {
        // default undo  restart strategy
        return  new SparkUndoRestart();
    }
}
