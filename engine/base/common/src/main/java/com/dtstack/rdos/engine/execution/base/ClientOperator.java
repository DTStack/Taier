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

    private Map<String, IClient> clientMap;

    private static ClientOperator singleton = new ClientOperator();

    private ClientOperator(){
        clientMap = JobSubmitExecutor.getInstance().getClientMap();
    }

    public static ClientOperator getInstance(){
        return singleton;
    }

    public RdosTaskStatus getJobStatus(String engineType, String jobId){

        if(Strings.isNullOrEmpty(jobId)){
            throw new RdosException("can't get job of jobId is empty or null!");
        }

        IClient client = clientMap.get(engineType);
        try{
            Object result = client.getJobStatus(jobId);

            if(result == null){
                return null;
            }

            RdosTaskStatus status = (RdosTaskStatus) result;

            if(status == RdosTaskStatus.FAILED){
                status = SlotJudge.judgeSlotsAndAgainExecute(engineType, jobId) ? RdosTaskStatus.WAITCOMPUTE : status;
            }

            return status;
        }catch (Exception e){
            LOG.error("", e);
            throw new RdosException("get job:" + jobId + " exception:" + e.getMessage());
        }
    }

    public String getEngineMessageByHttp(String engineType, String path){
        IClient client = clientMap.get(engineType);
        String message = "";
        try {
            message = client.getMessageByHttp(path);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return message;
    }

    public String getEngineLogByHttp(String engineType, String jobId) {
        IClient client = clientMap.get(engineType);
        String logInfo = "";
        try{
            logInfo = client.getJobLog(jobId);
        }catch (Exception e){
            LOG.error("", e);
        }

        return logInfo;
    }

    public Map<String, String> getJobMaster(){
        final Map<String, String> jobMasters = Maps.newConcurrentMap();
        clientMap.forEach((k,v)->{
            if(StringUtils.isNotBlank(v.getJobMaster())){
                try {
                    jobMasters.put(k, v.getJobMaster());
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
        });
        return jobMasters;
    }

}
