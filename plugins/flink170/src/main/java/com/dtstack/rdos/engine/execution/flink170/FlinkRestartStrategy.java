package com.dtstack.engine.dtscript.execution.flink170;

import com.dtstack.engine.dtscript.execution.base.IClient;
import com.dtstack.engine.dtscript.execution.base.restart.ARestartService;
import com.dtstack.engine.dtscript.execution.flink170.constrant.ExceptionInfoConstrant;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestartStrategy extends ARestartService {

    private final static String FLINK_EXCEPTION_URL = "/jobs/%s/exceptions";


    private static List<String> errorMsgs = ExceptionInfoConstrant.getNeedRestartException();

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

//    @Override
//    public boolean checkCanRestart(String jobId,String engineJobId, IClient client,
//                                   int alreadyRetryNum, int maxRetryNum) {
//        String reqURL = String.format(FLINK_EXCEPTION_URL, engineJobId);
//        String msg;
//        try {
//            msg = client.getMessageByHttp(reqURL);
//        } catch (Exception e){
//            msg = null;
//        }
//        return checkCanRestart(jobId, msg, alreadyRetryNum, maxRetryNum);
//    }
//
//    @Override
//    public boolean checkCanRestart(String jobId, String msg, int alreadyRetryNum, int maxRetryNum) {
//
//        boolean restart = false;
//        if(StringUtils.isNotBlank(msg)){
//            for(String emsg : errorMsgs){
//                if(msg.contains(emsg)){
//                    restart =  true;
//                    break;
//                }
//            }
//        }
//
//        if(restart){
//            return retry(jobId,alreadyRetryNum, maxRetryNum);
//        }else {
//            return false;
//        }
//    }
}
