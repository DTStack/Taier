package com.dtstack.engine.common;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2018/1/11
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClientOperator {

    private static final Logger LOG = LoggerFactory.getLogger(ClientOperator.class);

    private ClientCache clientCache = ClientCache.getInstance();

    private static ClientOperator singleton = new ClientOperator();

    private ClientOperator(){
    }

    public static ClientOperator getInstance(){
        return singleton;
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier){

        String jobId = jobIdentifier.getEngineJobId();
        if(Strings.isNullOrEmpty(jobId)){
            throw new RdosException("can't get job of jobId is empty or null!");
        }

        try{
            IClient client = clientCache.getClient(engineType, pluginInfo);
            Object result = client.getJobStatus(jobIdentifier);

            if(result == null){
                return null;
            }

            return  (RdosTaskStatus) result;
        }catch (Exception e){
            throw new RdosException("get job:" + jobId + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo){
        String message;

        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            message = client.getMessageByHttp(path);
        } catch (Exception e) {
            message = ExceptionUtil.getErrorMessage(e);
        }

        return message;
    }

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {

        String logInfo;

        try{
            IClient client = clientCache.getClient(engineType, pluginInfo);
            logInfo = client.getJobLog(jobIdentifier);
        }catch (Exception e){
            logInfo = ExceptionUtil.getErrorMessage(e);
        }

        return logInfo;
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier){

        try{
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getCheckpoints(jobIdentifier);
        }catch (Exception e){
            throw new RdosException("get job checkpoints:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier){
        try{
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getJobMaster(jobIdentifier);
        }catch (Exception e){
            throw new RdosException("get job master exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

}
