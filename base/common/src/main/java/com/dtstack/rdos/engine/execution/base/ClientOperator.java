package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.util.SlotJudge;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, String jobId){

        if(Strings.isNullOrEmpty(jobId)){
            throw new RdosException("can't get job of jobId is empty or null!");
        }

        try{
            IClient client = clientCache.getClient(engineType, pluginInfo);
            Object result = client.getJobStatus(jobId);

            if(result == null){
                return null;
            }

            RdosTaskStatus status = (RdosTaskStatus) result;

            if(status == RdosTaskStatus.FAILED){
                status = SlotJudge.judgeSlotsAndAgainExecute(engineType, jobId, pluginInfo) ? RdosTaskStatus.WAITCOMPUTE : status;
            }

            return status;
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
            logInfo = client.getJobLog(jobId);
        }catch (Exception e){
            LOG.error("", e);
            logInfo = e.toString();
        }

        return logInfo;
    }

}
