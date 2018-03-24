package com.dtstack.rdos.engine.execution.sparkyarn;

import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkRestartStrategy implements IRestartStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(SparkRestartStrategy.class);

    private final static String SPARK_ENGINE_DOWN = "Current state is not alive: STANDBY";

    private final static int RETRY_LIMIT = 3;

    private final static String analysisException = "org.apache.spark.sql.AnalysisException";

    private final static String parseException = "org.apache.spark.sql.catalyst.parser.ParseException";

    private Cache<String, Integer> retryJobCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 60, TimeUnit.SECONDS).build();


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
    public boolean checkCanRestart(String engineJobId, IClient client) {

        try {
            Integer retryNum = retryJobCache.get(engineJobId, ()-> 0);
            if(retryNum >= RETRY_LIMIT){
                return false;
            }
        } catch (ExecutionException e) {
            LOG.error("", e);
            return false;
        }

        String logInfo = client.getJobLog(engineJobId);
        if(Strings.isNullOrEmpty(logInfo)){
            return false;
        }

        if(logInfo.contains(analysisException) || logInfo.contains(parseException)){
            return false;
        }else{
            return true;
        }
    }

}
