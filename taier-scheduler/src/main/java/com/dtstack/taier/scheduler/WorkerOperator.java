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

package com.dtstack.taier.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.client.ClientOperator;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class WorkerOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerOperator.class);

    @Autowired
    private PluginWrapper pluginWrapper;

    @Autowired
    private ClientOperator clientOperator;

    private void buildPluginInfo(JobClient jobClient) {
        try {
            JSONObject info = JSONObject.parseObject(jobClient.getPluginInfo());
            if (null != info && !info.isEmpty()) {
                return;
            }
            pluginWrapper.wrapperJobClient(jobClient);
        } catch (Exception e) {
            LOGGER.error("{} buildPluginInfo failed!", jobClient.getJobId(), e);
            throw new RdosDefineException("buildPluginInfo error", e);
        }
    }

    private String getPluginInfo(JobIdentifier jobIdentifier) {
        if (null != jobIdentifier) {
            JSONObject info = JSONObject.parseObject(jobIdentifier.getPluginInfo());
            if (null != info && !info.isEmpty()) {
                return jobIdentifier.getPluginInfo();
            }
        }

        if (null == jobIdentifier || null == jobIdentifier.getTaskType() || null == jobIdentifier.getTenantId()) {
            LOGGER.error("pluginInfo params lost {}", jobIdentifier);
            throw new RdosDefineException("pluginInfo params lost");
        }
        Map<String, Object> info = pluginWrapper.wrapperPluginInfo(jobIdentifier.getTaskType(), jobIdentifier.getComponentVersion(), jobIdentifier.getTenantId(), jobIdentifier.getDeployMode(), jobIdentifier.getQueueName());
        if (null == info) {
            return null;
        }
        try {
            return PublicUtil.objToString(info);
        } catch (IOException e) {
            LOGGER.error("{} buildPluginInfo failed!", jobIdentifier.getJobId(), e);
        }
        return null;
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.judgeSlots(jobClient);
    }

    public JobResult submitJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.submitJob(jobClient);
    }

    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        TaskStatus status = clientOperator.getJobStatus(this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == status) {
            status = TaskStatus.NOTFOUND;
        }
        return status;
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        String engineLog = clientOperator.getEngineLog(this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == engineLog) {
            engineLog = StringUtils.EMPTY;
        }
        return engineLog;
    }

    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        List<String> rollingLogBaseInfo = clientOperator.getRollingLogBaseInfo(this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == rollingLogBaseInfo || rollingLogBaseInfo.size() == 0) {
            rollingLogBaseInfo = Lists.newArrayList();
        }
        return rollingLogBaseInfo;
    }

    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String checkPoints = clientOperator.getCheckpoints(this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == checkPoints) {
            checkPoints = StringUtils.EMPTY;
        }
        return checkPoints;
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.stopJob(jobClient);
    }

    public List<FileResult> listFile(String path, boolean isPathPattern, String pluginInfo) throws Exception {
        return clientOperator.listFile(path, isPathPattern, pluginInfo);
    }

    public CheckResult grammarCheck(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.grammarCheck(jobClient);
    }
}
