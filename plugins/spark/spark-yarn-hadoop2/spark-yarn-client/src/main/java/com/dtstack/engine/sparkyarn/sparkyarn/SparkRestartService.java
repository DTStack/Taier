package com.dtstack.engine.sparkyarn.sparkyarn;

import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.restart.ARestartService;
import com.dtstack.engine.common.restart.IJobRestartStrategy;
import com.dtstack.engine.sparkyarn.sparkyarn.restart.SparkUndoRestart;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkRestartService extends ARestartService {

    @Override
    public IJobRestartStrategy getAndParseErrorLog(String jobId, String engineJobId, String appId, IClient client) {
        // default undo  restart strategy
        return  new SparkUndoRestart();
    }
}
