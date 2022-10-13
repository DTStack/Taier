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

package com.dtstack.taier.dummy;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.sftp.SftpConfig;
import com.dtstack.taier.pluginapi.sftp.SftpFileManage;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * 用于流程上压测的dummy插件
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/4/13
 */
public class DummyClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(DummyClient.class);

    @Override
    public void init(Properties prop) throws Exception {
    }

    @Override
    public String getJobLog(JobIdentifier jobId) {
        Map<String, Object> jobLog = new HashMap<>(2);
        jobLog.put("jobId", jobId.getJobId());
        jobLog.put("msg_info", System.currentTimeMillis());
        return JSONObject.toJSONString(jobLog);
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.ok();
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return JobResult.createSuccessResult(jobIdentifier.getJobId(), jobIdentifier.getEngineJobId());
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return TaskStatus.FINISHED;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return StringUtils.EMPTY;
    }

    @Override
    public String getMessageByHttp(String path) {
        return StringUtils.EMPTY;
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return JobResult.createSuccessResult(jobClient.getJobId(), jobClient.getJobId());
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            SftpConfig sftpConfig = PublicUtil.jsonStrToObject(pluginInfo, SftpConfig.class);
            // check sftpConfig 准确性
            SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
            //测试路径是否存在
            Vector res = sftpFileManage.listFile(sftpConfig.getPath());
            if (null != res) {
                componentTestResult.setResult(true);
            }
        } catch (Exception e) {
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            componentTestResult.setResult(false);
        }
        return componentTestResult;
    }
}
