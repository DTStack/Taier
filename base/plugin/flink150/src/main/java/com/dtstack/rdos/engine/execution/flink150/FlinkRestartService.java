package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.restart.ARestartService;
import com.dtstack.rdos.engine.execution.base.restart.IJobRestartStrategy;
import com.dtstack.rdos.engine.execution.flink150.constrant.ExceptionInfoConstrant;
import com.dtstack.rdos.engine.execution.flink150.restart.FlinkAddMemoryRestart;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Reason:  重启服务，根据日志返回重启策略
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestartService extends ARestartService {

    private static Logger logger = LoggerFactory.getLogger(FlinkRestartService.class);

    private final static String FLINK_EXCEPTION_URL = "/jobs/%s/exceptions";

    private final static List<String> unrestartExceptionList = Lists.newArrayList(EngineResourceInfo.LIMIT_RESOURCE_ERROR);
    private final static List<String> restartExceptionList = ExceptionInfoConstrant.getNeedRestartException();

    private Cache<String, String> jobErrorInfoCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(5 * 60, TimeUnit.SECONDS).build();

    @Override
    public boolean checkFailureForEngineDown(String msg) {
        if(StringUtils.isNotBlank(msg) && msg.contains(ExceptionInfoConstrant.FLINK_ENGINE_DOWN_RESTART_EXCEPTION)){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        if(StringUtils.isNotBlank(msg) && msg.contains(ExceptionInfoConstrant.FLINK_NO_RESOURCE_AVAILABLE_RESTART_EXCEPTION)){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkCanRestart(String jobId, String engineJobId, String appId,IClient client,
                                   int alreadyRetryNum, int maxRetryNum) {

        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, engineJobId);
        String msg = getErrorMsgByURL(jobIdentifier, client);
        return checkCanRestart(jobId, msg, alreadyRetryNum, maxRetryNum);
    }

    public String getErrorMsgByURL(JobIdentifier jobIdentifier, IClient client) {
        String errorLog = null;
        try {
            errorLog = jobErrorInfoCache.get(jobIdentifier.getEngineJobId(), () -> {
                String msg;
                try {
                    msg = client.getJobLog(jobIdentifier);
                } catch (Exception e) {
                    msg = null;
                }
                return msg;
            });
        } catch (ExecutionException e) {
            logger.error("get error log failed ..", e);
        }

        return errorLog;
    }

    @Override
    public boolean checkCanRestart(String jobId, String msg, int alreadyRetryNum, int maxRetryNum) {

        boolean restart = false;

        IJobRestartStrategy iJobRestartStrategy = parseErrorLog(msg);

        restart = iJobRestartStrategy == null ? false : true;

        if(restart){
            return retry(jobId,alreadyRetryNum, maxRetryNum);
        }else {
            return false;
        }
    }

    public IJobRestartStrategy parseErrorLog(String msg) {
        IJobRestartStrategy strategy = null;
        if (StringUtils.isNotBlank(msg)) {
            for (String emsg : restartExceptionList) {
                if (msg.contains(emsg)) {
                    strategy = new FlinkAddMemoryRestart();
                    break;
                }
            }
        }
        return strategy;
    }

    @Override
    public IJobRestartStrategy getAndParseErrorLog(String jobId, String engineJobId, String appId, IClient client) {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, engineJobId);
        String msg = getErrorMsgByURL(jobIdentifier, client);
        return parseErrorLog(msg);
    }


    public boolean retry(String jobId, int alreadyRetryNum, int maxRetryNum){
        return alreadyRetryNum < maxRetryNum;
    }
}
