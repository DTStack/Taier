package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
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

        String jobId = jobIdentifier.getJobId();
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
            LOG.error("", e);
            throw new RdosException("get job:" + jobId + " exception:" + e.getMessage());
        }
    }

    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo){
        String message = "";
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            message = client.getMessageByHttp(path);
        } catch (Exception e) {
            LOG.error("", e);
            message = e.toString();
        }
        return message;
    }

    public String getEngineLog(String engineType, String pluginInfo, String jobId) {

        String logInfo = "";

        try{
            IClient client = clientCache.getClient(engineType, pluginInfo);
            logInfo = client.getJobLog(JobIdentifier.createInstance(jobId, null));
        }catch (Exception e){
            LOG.error("", e);
            logInfo = e.toString();
        }

        return logInfo;
    }

}
