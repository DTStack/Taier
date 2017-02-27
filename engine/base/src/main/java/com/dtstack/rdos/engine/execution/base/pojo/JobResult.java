package com.dtstack.rdos.engine.execution.base.pojo;

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

    private JSONObject json = new JSONObject();

    public static JobResult newInstance(boolean isErr){
        JobResult result = new JobResult();
        result.isErr = isErr;
        return  result;
    }

    public static JobResult createErrorResult(Exception e){
        JobResult jobResult = JobResult.newInstance(true);
        jobResult.setData("errMsg", e.getMessage());
        return jobResult;
    }

    public static JobResult createErrorResult(String errMsg){
        JobResult jobResult = JobResult.newInstance(true);
        jobResult.setData("errMsg", errMsg);
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

    public JSONObject getJson() {
        return json;
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
