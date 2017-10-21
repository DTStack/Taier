package com.dtstack.rdos.engine.execution.base.pojo;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobResult {

    private static final Logger logger = LoggerFactory.getLogger(JobResult.class);
    
    private boolean isErr;

    public static final String JOB_ID_KEY = "jobid";

    public static final String MSG_INFO = "msg_info";

    private JSONObject json = new JSONObject();

    public static JobResult newInstance(boolean isErr){
        JobResult result = new JobResult();
        result.isErr = isErr;
        return  result;
    }

    public static JobResult createErrorResult(Throwable e){
        JobResult jobResult = JobResult.newInstance(true);
        String errMsg = ExceptionUtil.getErrorMessage(e);
        jobResult.setData(MSG_INFO, errMsg);
        return jobResult;
    }

    public static JobResult createErrorResult(String errMsg){
        JobResult jobResult = JobResult.newInstance(true);
        jobResult.setData(MSG_INFO, errMsg);
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId){
        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(JOB_ID_KEY, taskId);
        jobResult.setData(MSG_INFO, "submit job is success");
        return jobResult;
    }

    public boolean setData(String key, String value){
        try{
            json.put(key, value);
            return true;
        }catch (Exception e){
            logger.error("", e);
            return false;
        }
    }

    public String getData(String key){

        if(!json.has(key)){
            return null;
        }

        try {
            return (String) json.get(key);
        } catch (JSONException e) {
            logger.error("", e);
            return null;
        }
    }

    public JSONObject getJson() {
        return json;
    }

    public String getJsonStr(){
        return json.toString();
    }

    public boolean isErr() {
        return isErr;
    }

    public void setErr(boolean err) {
        isErr = err;
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
