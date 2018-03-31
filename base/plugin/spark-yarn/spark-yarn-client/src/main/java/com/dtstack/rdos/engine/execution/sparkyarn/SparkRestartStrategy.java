package com.dtstack.rdos.engine.execution.sparkyarn;

import com.clearspring.analytics.util.Lists;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkRestartStrategy implements IRestartStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(SparkRestartStrategy.class);

    private final static String SPARK_ENGINE_DOWN = "Current state is not alive: STANDBY";

    /**FIXME 测试暂时改成一次*/
    private final static int RETRY_LIMIT = 1;

    private final static String analysisException = "org.apache.spark.sql.AnalysisException";

    private final static String metadataFetchException = "org.apache.spark.shuffle.MetadataFetchFailedException";

    private final static String parseException = "org.apache.spark.sql.catalyst.parser.ParseException";

    private final static String analysisSubException = ".*org\\.apache\\.spark\\.sql\\.catalyst\\.analysis\\.\\w+Exception";

    private final static List<String> exceptionList = Lists.newArrayList();

    private final static List<Pattern> exceptionPatternList = Lists.newArrayList();

    static {
        exceptionList.add(analysisException);
        exceptionList.add(parseException);
        exceptionList.add(metadataFetchException);

        exceptionPatternList.add(Pattern.compile(analysisSubException));
    }

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

            retryNum++;
            retryJobCache.put(engineJobId, retryNum);
        } catch (ExecutionException e) {
            LOG.error("", e);
            return false;
        }

        String logInfo = client.getJobLog(engineJobId);
        if(Strings.isNullOrEmpty(logInfo)){
            return false;
        }

        for(String excepStr : exceptionList){
            if(logInfo.contains(excepStr)){
                return false;
            }
        }

        for(Pattern pattern : exceptionPatternList){
            Matcher matcher = pattern.matcher(logInfo);
            if(matcher.find()){
                return false;
            }
        }

        return true;
    }

}
