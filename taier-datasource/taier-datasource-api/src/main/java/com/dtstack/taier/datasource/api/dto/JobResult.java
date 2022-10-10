package com.dtstack.taier.datasource.api.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 专门提供给job接口使用的返回类
 *
 * @author luming
 * @date 2022/3/10
 */
public class JobResult implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobResult.class);
    private static final long serialVersionUID = 1L;
    private boolean checkRetry;
    public static final String JOB_ID_KEY = "jobid";
    public static final String EXT_ID_KEY = "extid";
    public static final String MSG_INFO = "msg_info";
    private JSONObject json = new JSONObject();
    private JSONObject extraInfoJson = new JSONObject();

    public JobResult() {
    }

    public static JobResult newInstance(boolean checkRetry) {
        JobResult result = new JobResult();
        result.checkRetry = checkRetry;
        return result;
    }

    public static JobResult createErrorResult(Throwable e) {
        JobResult jobResult = newInstance(true);
        jobResult.setData("msg_info", e.getMessage());
        return jobResult;
    }

    public static JobResult createErrorResult(boolean checkRetry, Throwable e) {
        JobResult jobResult = newInstance(checkRetry);
        jobResult.setData("msg_info", e.getMessage());
        return jobResult;
    }

    public static JobResult createErrorResult(boolean checkRetry, String errMsg) {
        JobResult jobResult = newInstance(checkRetry);
        jobResult.setData("msg_info", errMsg);
        return jobResult;
    }

    public static JobResult createErrorResult(String errMsg) {
        JobResult jobResult = newInstance(true);
        jobResult.setData("msg_info", errMsg);
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId) {
        JobResult jobResult = newInstance(false);
        jobResult.setData("jobid", taskId);
        jobResult.setData("msg_info", "submit job is success");
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId, String extId) {
        JobResult jobResult = createSuccessResult(taskId);
        if (!Strings.isNullOrEmpty(extId)) {
            jobResult.setData("extid", extId);
        }

        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId, String extId, String jobGraph) {
        JobResult jobResult = createSuccessResult(taskId);
        if (!Strings.isNullOrEmpty(extId)) {
            jobResult.setData("extid", extId);
        }

        if (!StringUtils.isEmpty(jobGraph)) {
            jobResult.setExtraData("job_graph", jobGraph);
        }

        return jobResult;
    }

    public String getMsgInfo() {
        return !this.json.containsKey("msg_info") ? "" : this.json.getString("msg_info");
    }

    public boolean setData(String key, String value) {
        try {
            this.json.put(key, value);
            return true;
        } catch (Exception var4) {
            LOGGER.error("", var4);
            return false;
        }
    }

    public boolean setExtraData(String key, String value) {
        try {
            this.extraInfoJson.put(key, value);
            return true;
        } catch (Exception var4) {
            LOGGER.error("", var4);
            return false;
        }
    }

    public String getData(String key) {
        return !this.json.containsKey(key) ? null : this.json.getString(key);
    }

    public JSONObject getJson() {
        return this.json;
    }

    public JSONObject getExtraInfoJson() {
        return this.extraInfoJson;
    }

    public void setExtraInfoJson(JSONObject extraInfoJson) {
        this.extraInfoJson = extraInfoJson;
    }

    public String getJsonStr() {
        return this.json.toString();
    }

    public boolean getCheckRetry() {
        return this.checkRetry;
    }

    public void setCheckRetry(boolean checkRetry) {
        this.checkRetry = checkRetry;
    }

    public String toString() {
        return this.json.toString();
    }
}
