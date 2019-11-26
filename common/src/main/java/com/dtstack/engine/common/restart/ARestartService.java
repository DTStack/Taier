package com.dtstack.engine.common.restart;

import com.dtstack.engine.common.IClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * 各个插件对失败作业的重启策略
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class ARestartService {

    private static Logger logger = LoggerFactory.getLogger(ARestartService.class);

    private final static Integer RETRY_LIMIT = 3;

    /**判断引擎是不是挂了*/
    public abstract boolean checkFailureForEngineDown(String msg);

    public abstract boolean checkNOResource(String msg);

    public boolean checkCanRestart(String jobId, String engineJobId, String appId, IClient client,
                                            int alreadyRetryNum, int maxRetryNum) {
        return retry(jobId, alreadyRetryNum, maxRetryNum);
    }

    public boolean checkCanRestart(String jobId, String msg, int alreadyRetryNum, int maxRetryNum) {
        return retry(jobId, alreadyRetryNum, maxRetryNum);
    }

    public boolean retrySubmitFail(String jobId, String msg, int alreadyRetryNum, int maxRetryNum){
        if(checkFailureForEngineDown(msg)){
            return true;
        }

        return checkCanRestart(jobId, msg, alreadyRetryNum, maxRetryNum);
    }

    public boolean retry(String jobId, int alreadyRetryNum, int maxRetryNum){
        return alreadyRetryNum < maxRetryNum;
    }

    public IJobRestartStrategy getAndParseErrorLog(String jobId, String engineJobId, String appId, IClient client) {
        return null;
    };

}
