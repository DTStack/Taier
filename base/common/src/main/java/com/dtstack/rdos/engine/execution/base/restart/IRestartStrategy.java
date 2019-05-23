package com.dtstack.rdos.engine.execution.base.restart;

import com.dtstack.rdos.engine.execution.base.IClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.concurrent.TimeUnit;

/**
 * 各个插件对失败作业的重启策略
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class IRestartStrategy {

    private static Logger logger = LoggerFactory.getLogger(IRestartStrategy.class);

    private final static Integer RETRY_LIMIT = 3;

    /**判断引擎是不是挂了*/
    public abstract boolean checkFailureForEngineDown(String msg);

    public abstract boolean checkNOResource(String msg);

    public boolean checkCanRestart(String jobId, String engineJobId, IClient client,
                                            Integer alreadyRetryNum, Integer maxRetryNum){
        return retry(jobId, alreadyRetryNum, maxRetryNum);
    }

    public boolean checkCanRestart(String jobId, String msg, Integer alreadyRetryNum, Integer maxRetryNum){
        return retry(jobId, alreadyRetryNum, maxRetryNum);
    }

    public boolean retrySubmitFail(String jobId, String msg, Integer alreadyRetryNum, Integer maxRetryNum){
        if(checkFailureForEngineDown(msg)){
            return true;
        }

        return checkCanRestart(jobId, msg, alreadyRetryNum, maxRetryNum);
    }

    public boolean retry(String jobId, Integer alreadyRetryNum, Integer maxRetryNum){
        try {
            Integer retry_limit_real = maxRetryNum != null ? maxRetryNum : RETRY_LIMIT;
            if(alreadyRetryNum >= retry_limit_real){
                return false;
            }
        }catch (Throwable e) {
            logger.error("retry again:", e);
            return false;
        }
        return true;
    }
}
