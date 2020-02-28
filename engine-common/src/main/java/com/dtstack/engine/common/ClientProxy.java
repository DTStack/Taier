package com.dtstack.engine.common;

import com.dtstack.engine.common.callback.CallBack;
import com.dtstack.engine.common.callback.ClassLoaderCallBackMethod;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.restart.RestartStrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 代理IClient实现类的proxy
 * Date: 2017/12/19
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClientProxy implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(ClientProxy.class);

    private IClient targetClient;

    public ClientProxy(IClient targetClient){
        this.targetClient = targetClient;
    }

    @Override
    public void init(Properties prop) throws Exception {
        ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>(){
            @Override
            public String execute() throws Exception {
                 targetClient.init(prop);
                 return null;
            }
        }, targetClient.getClass().getClassLoader(),true);
    }

    @Override
    public JobResult submitJob(JobClient jobClient){
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>(){

                @Override
                public JobResult execute() throws Exception {
                    return targetClient.submitJob(jobClient);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>(){

                @Override
                public JobResult execute() throws Exception {
                    return targetClient.cancelJob(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<RdosTaskStatus>(){

                @Override
                public RdosTaskStatus execute() throws Exception {
                    return targetClient.getJobStatus(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getJobMaster(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public String getMessageByHttp(String path) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getMessageByHttp(path);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getJobLog(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<Boolean>(){

                @Override
                public Boolean execute() throws Exception {
                    return targetClient.judgeSlots(jobClient);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            if (e instanceof ClientArgumentException) {
                throw new ClientArgumentException(e);
            } else if (e instanceof LimitResourceException) {
                throw new LimitResourceException(e.getMessage());
            } else if (e instanceof RdosDefineException && ((RdosDefineException) e).getErrorCode() == ErrorCode.HTTP_CALL_ERROR){
                return false;
            }
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<List<String>>(){

                @Override
                public List<String> execute() throws Exception {
                    return targetClient.getContainerInfos(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getCheckpoints(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public RestartStrategyType getRestartStrategyType(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<RestartStrategyType>(){

                @Override
                public RestartStrategyType execute() throws Exception {
                    return targetClient.getRestartStrategyType(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }


}
