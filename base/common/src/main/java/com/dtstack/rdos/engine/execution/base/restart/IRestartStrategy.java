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

    protected Cache<String, Integer> retryJobCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 60, TimeUnit.SECONDS).build();

    private final static Integer RETRY_LIMIT = 3;

    /**判断引擎是不是挂了*/
    public abstract boolean checkFailureForEngineDown(String msg);

    public abstract boolean checkNOResource(String msg);

    public abstract boolean checkCanRestart(String jobId,String engineJobId, IClient client);

    public abstract boolean checkCanRestart(String jobId, String msg);

    public boolean retrySubmitFail(String jobId, String msg, Integer retryNum){
        if(checkFailureForEngineDown(msg)){
            return true;
        }

        return checkCanRestart(jobId, msg);
    }

    public boolean retry(String jobId,Integer retryNum){
        try {
            Integer retry_limit_real = retryNum != null ? retryNum : RETRY_LIMIT;
            Integer rms = retryJobCache.get(jobId, ()-> 0);
            if(rms >= retry_limit_real){
                retryJobCache.asMap().remove(jobId);
                return false;
            }
            retryJobCache.put(jobId,++rms);
        }catch (Throwable e) {
            logger.error("retry again:", e);
            return false;
        }
        return true;
    }
}
