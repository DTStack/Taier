package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.restart.ARestartService;
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
        ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<String>(){
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
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<JobResult>(){

                @Override
                public JobResult execute() throws Exception {
                    return targetClient.submitJob(jobClient);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<JobResult>(){

                @Override
                public JobResult execute() throws Exception {
                    return targetClient.cancelJob(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<RdosTaskStatus>(){

                @Override
                public RdosTaskStatus execute() throws Exception {
                    return targetClient.getJobStatus(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public String getJobMaster() {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getJobMaster();
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public String getMessageByHttp(String path) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getMessageByHttp(path);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getJobLog(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public EngineResourceInfo getAvailSlots(JobClient jobClient) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<EngineResourceInfo>(){

                @Override
                public EngineResourceInfo execute() throws Exception {
                    return targetClient.getAvailSlots(jobClient);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<List<String>>(){

                @Override
                public List<String> execute() throws Exception {
                    return targetClient.getContainerInfos(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<String>(){

                @Override
                public String execute() throws Exception {
                    return targetClient.getCheckpoints(jobIdentifier);
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }

    @Override
    public ARestartService getRestartService() {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<ARestartService>(){

                @Override
                public ARestartService execute() throws Exception {
                    return targetClient.getRestartService();
                }
            }, targetClient.getClass().getClassLoader(),true);
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }
    }


}
