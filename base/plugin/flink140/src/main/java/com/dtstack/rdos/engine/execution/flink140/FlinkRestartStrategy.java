package com.dtstack.rdos.engine.execution.flink140;

import avro.shaded.com.google.common.collect.Lists;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestartStrategy extends IRestartStrategy {

    private final static String FLINK_EXCEPTION_URL = "/jobs/%s/exceptions";

    private final static String FLINK_ENGINE_DOWN = "Could not connect to the leading JobManager";

    private final static String FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION = "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job";

    private final static String FLINK_TASK_LOST = "TaskManager was lost/killed";

    private final static String JDBC_LINK_FAILURE = "com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure";

    private final static String JOBMGR_NOT_RESPONSE = "JobTimeoutException: JobManager did not respond within";

    private static List<String> errorMsgs = Lists.newArrayList(FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION, FLINK_ENGINE_DOWN, FLINK_TASK_LOST, JDBC_LINK_FAILURE, JOBMGR_NOT_RESPONSE);

    @Override
    public boolean checkFailureForEngineDown(String msg) {
        if(StringUtils.isNotBlank(msg) && msg.contains(FLINK_ENGINE_DOWN)){
            return true;
        }

        return false;
    }

    @Override
    public boolean checkNOResource(String msg) {
        if(StringUtils.isNotBlank(msg) && msg.contains(FLINK_NO_RESOURCE_AVAILABLE_EXCEPTION)){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkCanRestart(String jobId,String engineJobId, IClient client) {
        boolean restart = false;
        String reqURL = String.format(FLINK_EXCEPTION_URL, engineJobId);
        String msg = client.getMessageByHttp(reqURL);
        if(StringUtils.isNotBlank(msg)){
            for(String emsg:errorMsgs){
                if(msg.contains(emsg)){
                    restart =  true;
                    break;
                }
            }
        }
        if(restart){
            return retry(jobId,null);
        }else {
            return false;
        }
    }
}
