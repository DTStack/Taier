package com.dtstack.rdos.engine.execution.sparkyarn;

import com.clearspring.analytics.util.Lists;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkRestartStrategy extends IRestartStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(SparkRestartStrategy.class);

    private final static String SPARK_ENGINE_DOWN = "Current state is not alive: STANDBY";

    private final static String treeNodeException = "org.apache.spark.sql.catalyst.errors.package$TreeNodeException: execute, tree";

    private final static List<String> exceptionList = Lists.newArrayList();

    static {
        exceptionList.add(treeNodeException);
    }

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
    public boolean checkCanRestart(String jobId,String engineJobId, IClient client) {
        String msg = client.getJobLog(engineJobId);
        return checkCanRestart(jobId, msg);
    }

    @Override
    public boolean checkCanRestart(String jobId, String msg) {
        boolean restart = false;
        if(StringUtils.isNotBlank(msg)){
            for(String emsg : exceptionList){
                if(msg.contains(emsg)){
                    restart =  true;
                    break;
                }
            }
        }
        if(restart){
            return retry(jobId,null);
        }else {
            return false;
        }
    }
}
