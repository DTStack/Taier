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

package com.dtstack.taier.pluginapi.client;

import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public abstract class AbstractClient implements IClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);

    public AbstractClient() {
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try {
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null) {
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(), ",") + ")");
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            jobResult = JobResult.createErrorResult(e);
        } finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        TaskStatus status = TaskStatus.NOTFOUND;
        try {
            status = processJobStatus(jobIdentifier);
        } catch (Exception e) {
            LOGGER.error("get job status error: {}", e.getMessage());
        } finally {
            handleJobStatus(jobIdentifier, status);
        }
        return status;
    }

    protected TaskStatus processJobStatus(JobIdentifier jobIdentifier) {
        return TaskStatus.NOTFOUND;
    }

    protected void handleJobStatus(JobIdentifier jobIdentifier, TaskStatus status) {
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        return null;
    }

    /**
     * job 处理具体实现的抽象
     *
     * @param jobClient 对象参数
     * @return 处理结果
     */
    protected abstract JobResult processSubmitJobWithType(JobClient jobClient);

    @Override
    public String getJobLog(JobIdentifier jobId) {
        return "";
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.notOk("");
    }

    protected void beforeSubmitFunc(JobClient jobClient) {
    }

    protected void afterSubmitFunc(JobClient jobClient) {
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }


    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }


    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        return componentTestResult;
    }

    @Override
    public CheckResult grammarCheck(JobClient jobClient) {
        return null;
    }

    @Override
    public List<FileResult> listFile(String path, boolean isPathPattern) {
        return null;
    }
}
