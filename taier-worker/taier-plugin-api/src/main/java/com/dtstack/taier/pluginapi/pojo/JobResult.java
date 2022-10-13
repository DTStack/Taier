/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.pluginapi.pojo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class JobResult implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResult.class);

    private static final long serialVersionUID = 1L;

    private boolean checkRetry;

    public static final String JOB_ID_KEY = "appId";

    public static final String EXT_ID_KEY = "extId";

    public static final String MSG_INFO = "msg_info";

    private JSONObject json = new JSONObject();

    /**
     * schedule_job 中 job_extra_info 字段数据
     */
    private JSONObject extraInfoJson = new JSONObject();

    public static JobResult newInstance(boolean checkRetry) {
        JobResult result = new JobResult();
        result.checkRetry = checkRetry;
        return result;
    }

    public static JobResult createErrorResult(Throwable e) {
        JobResult jobResult = JobResult.newInstance(true);
        String errMsg = ExceptionUtil.getErrorMessage(e);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createErrorResult(boolean checkRetry, Throwable e) {
        JobResult jobResult = JobResult.newInstance(checkRetry);
        String errMsg = ExceptionUtil.getErrorMessage(e);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createErrorResult(boolean checkRetry, String errMsg) {
        JobResult jobResult = JobResult.newInstance(checkRetry);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createErrorResult(String errMsg) {
        JobResult jobResult = JobResult.newInstance(true);
        jobResult.setData(MSG_INFO, addTimeForMsg(errMsg));
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId) {
        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(JOB_ID_KEY, taskId);
        jobResult.setData(MSG_INFO, addTimeForMsg("submit job is success"));
        return jobResult;
    }

    public static JobResult createSuccessResult(String taskId, String extId) {
        JobResult jobResult = createSuccessResult(taskId);
        if (!Strings.isNullOrEmpty(extId)) {
            jobResult.setData(EXT_ID_KEY, extId);
        }

        return jobResult;
    }

    public String getMsgInfo() {
        if (!json.containsKey(MSG_INFO)) {
            return "";
        }

        return json.getString(MSG_INFO);
    }

    public boolean setData(String key, String value) {
        try {
            json.put(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("", e);
            return false;
        }
    }


    public boolean setExtraData(String key, String value) {
        try {
            extraInfoJson.put(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("", e);
            return false;
        }
    }

    public String getData(String key) {

        if (!json.containsKey(key)) {
            return null;
        }

        return json.getString(key);
    }

    public static String addTimeForMsg(String msg) {
        return DateUtil.timestampToString(new Date()) + ":" + msg;
    }

    public JSONObject getJson() {
        return json;
    }

    public JSONObject getExtraInfoJson() {
        return extraInfoJson;
    }

    public void setExtraInfoJson(JSONObject extraInfoJson) {
        this.extraInfoJson = extraInfoJson;
    }

    public String getJsonStr() {
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
