package com.dtstack.rdos.engine.execution.spark160.sparkyarn;

import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import com.dtstack.rdos.engine.execution.spark160.sparkyarn.enums.ExceptionInfoConstrant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SparkRestartStrategy extends IRestartStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(SparkRestartStrategy.class);

    private final static List<String> exceptionList = ExceptionInfoConstrant.getNeedRestartException();

    @Override
    public boolean checkFailureForEngineDown(String msg) {
        if(msg != null && msg.contains(ExceptionInfoConstrant.SPARK_ENGINE_DOWN_RESTART_EXCEPTION)){
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
        /*boolean restart = false;
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
        }*/

        return retry(jobId,null);
    }
}
