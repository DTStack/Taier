package com.dtstack.rdos.engine.execution.flink150.restart;

import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.restart.IExtractStrategy;
import com.dtstack.rdos.engine.execution.base.restart.IRestartService;
import com.dtstack.rdos.engine.execution.flink150.constrant.ExceptionInfoConstrant;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description:   每个插件应该具有日志解析的服务，用来返回某种重启策略
 * @author: maqi
 * @create: 2019/07/17 16:19
 */
public class FlinkRestartService implements IRestartService {

    private final static List<String> unrestartExceptionList = Lists.newArrayList(EngineResourceInfo.LIMIT_RESOURCE_ERROR);
    private final static List<String> restartExceptionList = ExceptionInfoConstrant.getNeedRestartException();

    @Override
    public IExtractStrategy parseErrorLog(String engineJobId, IClient client) {
        String msg = client.getJobLog(JobIdentifier.createInstance(engineJobId, null, null));
        return getRestartStrategyFromLog(msg);
    }

    public boolean checkCanRestart(String jobId, String engineJobId, IClient client,
                                   int alreadyRetryNum, int maxRetryNum) {
        return retry(jobId, alreadyRetryNum, maxRetryNum);
    }


    public IExtractStrategy getRestartStrategyFromLog(String msg) {
        IExtractStrategy strategy = null;

        if(StringUtils.isNotBlank(msg)){
            for(String emsg : restartExceptionList){
                if(msg.contains(emsg)){
                    strategy = new FlinkAddMemoryRestart();
                    break;
                }
            }
            //other error type
        }
        return strategy;
    }


    public boolean retry(String jobId, int alreadyRetryNum, int maxRetryNum){
        return alreadyRetryNum < maxRetryNum;
    }
}