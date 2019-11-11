package com.dtstack.engine.spark160.sparkyarn;

import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.restart.ARestartService;
import com.dtstack.engine.spark160.sparkyarn.enums.ExceptionInfoConstrant;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SparkRestartStrategy extends ARestartService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SparkRestartStrategy.class);

    private final static List<String> unrestartExceptionList = Lists.newArrayList(EngineResourceInfo.LIMIT_RESOURCE_ERROR);
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

//    @Override
//    public boolean checkCanRestart(String jobId, String engineJobId, IClient client,
//                                   int alreadyRetryNum, int maxRetryNum) {
//        String msg = client.getJobLog(JobIdentifier.createInstance(engineJobId, null, null));
//        return checkCanRestart(jobId, msg, alreadyRetryNum, maxRetryNum);
//    }
//
//    @Override
//    public boolean checkCanRestart(String jobId, String msg, int alreadyRetryNum, int maxRetryNum) {
//        boolean restart = true;
//        if(StringUtils.isNotBlank(msg)){
//            for(String emsg : unrestartExceptionList){
//                if(msg.contains(emsg)){
//                    restart =  false;
//                    break;
//                }
//            }
//        }
//
//        if(restart){
//            return retry(jobId, alreadyRetryNum, maxRetryNum);
//        }else {
//            return false;
//        }
//    }
}
