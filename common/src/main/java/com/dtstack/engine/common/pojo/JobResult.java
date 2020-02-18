package com.dtstack.engine.common.pojo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobResult {

    private static final Logger logger = LoggerFactory.getLogger(JobResult.class);

    private boolean checkRetry;

    public static final String JOB_ID_KEY = "jobid";

    public static final String EXT_ID_KEY = "extid";

    public static final String MSG_INFO = "msg_info";

    private JSONObject json = new JSONObject();

    public static JobResult newInstance(boolean checkRetry){
        JobResult result = new JobResult();
        result.checkRetry = checkRetry;
        return  result;
    }

    public static JobResult createErrorResult(Throwable e){
        JobResult jobResult = JobResult.newInstance(true);
        String errMsg = ExceptionUtil.getErrorMessage(e);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createErrorResult(boolean checkRetry, Throwable e){
        JobResult jobResult = JobResult.newInstance(checkRetry);
        String errMsg = ExceptionUtil.getErrorMessage(e);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createErrorResult(String errMsg){
        JobResult jobResult = JobResult.newInstance(true);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId){
        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(JOB_ID_KEY, taskId);
        jobResult.setData(MSG_INFO, addTimeForMsg("submit job is success"));
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId, String extId){
        JobResult jobResult = createSuccessResult(taskId);
        if(!Strings.isNullOrEmpty(extId)){
            jobResult.setData(EXT_ID_KEY, extId);
        }

        return jobResult;
    }

    public String getMsgInfo() {
        if(!json.containsKey(MSG_INFO)){
            return "";
        }

        return json.getString(MSG_INFO);
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

        if(!json.containsKey(key)){
            return null;
        }

        return json.getString(key);
    }

    public static String addTimeForMsg(String msg){
        return DateUtil.timestampToString(new Date())+":"+msg;
    }

    public JSONObject getJson() {
        return json;
    }

    public String getJsonStr(){
        return json.toString();
    }

    public boolean getCheckRetry() {
        return checkRetry;
    }

    public void setCheckRetry(boolean checkRetry) {
        this.checkRetry = checkRetry;
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
