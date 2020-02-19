package com.dtstack.engine.flink;

import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.restart.CommonRestartService;
import com.dtstack.engine.common.restart.IJobRestartStrategy;
import com.dtstack.engine.flink.constrant.ExceptionInfoConstrant;
import com.dtstack.engine.flink.restart.FlinkAddMemoryRestart;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestartService extends CommonRestartService {

    private static Logger logger = LoggerFactory.getLogger(FlinkRestartService.class);

    private final static List<String> ADD_MEM_RESTART_EXCEPTION = ExceptionInfoConstrant.getNeedAddMemRestartException();

    private Cache<String, String> jobErrorInfoCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(5 * 60, TimeUnit.SECONDS).build();

    public String getErrorMsgByUrl(JobIdentifier jobIdentifier, IClient client) {
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

    public IJobRestartStrategy parseErrorLog(String msg) {
        if (StringUtils.isNotBlank(msg)) {
            for (String emsg : ADD_MEM_RESTART_EXCEPTION) {
                if (msg.contains(emsg)) {
                    return new FlinkAddMemoryRestart();
                }
            }
        }
        return null;
    }

    @Override
    public IJobRestartStrategy getAndParseErrorLog(String jobId, String engineJobId, String appId, IClient client) {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobId, appId, jobId);
        String msg = getErrorMsgByUrl(jobIdentifier, client);
        return parseErrorLog(msg);
    }

}
