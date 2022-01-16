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

package com.dtstack.engine.master;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.pojo.ClusterResource;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.pluginapi.pojo.JobResult;
import com.dtstack.engine.pluginapi.pojo.JudgeResult;
import com.dtstack.engine.pluginapi.util.PublicUtil;
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
        Map<String, Object> info = pluginWrapper.wrapperPluginInfo(jobIdentifier.getTaskType(), jobIdentifier.getComponentVersion(), jobIdentifier.getTenantId(), jobIdentifier.getDeployMode());
        if(null == info){
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

    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        String taskName = EScheduleJobType.getTaskType(jobIdentifier.getTaskType()).name();
        RdosTaskStatus status = clientOperator.getJobStatus(taskName, this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == status) {
            status = RdosTaskStatus.NOTFOUND;
        }
        return status;
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        String taskName = EScheduleJobType.getTaskType(jobIdentifier.getTaskType()).name();
        String engineLog = clientOperator.getEngineLog(taskName, this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == engineLog) {
            engineLog = StringUtils.EMPTY;
        }
        return engineLog;
    }

    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String taskName = EScheduleJobType.getTaskType(jobIdentifier.getTaskType()).name();
        String checkPoints = clientOperator.getCheckpoints(taskName, this.getPluginInfo(jobIdentifier), jobIdentifier);
        if (null == checkPoints) {
            checkPoints = StringUtils.EMPTY;
        }
        return checkPoints;
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        this.buildPluginInfo(jobClient);
        return clientOperator.stopJob(jobClient);
    }

    public ComponentTestResult testConnect(String engineType, String pluginInfo) {
        ComponentTestResult testResult = clientOperator.testConnect(engineType, pluginInfo);
        if (null == testResult) {
            testResult = new ComponentTestResult();
        }
        return testResult;
    }


    public List<List<Object>> executeQuery(String engineType, String pluginInfo, String sql, String database) throws Exception {
        return clientOperator.executeQuery(engineType, pluginInfo, sql, database);
    }

    public String uploadStringToHdfs(String engineType, String pluginInfo, String bytes, String hdfsPath) throws Exception {
        return clientOperator.uploadStringToHdfs(engineType, pluginInfo, bytes, hdfsPath);
    }

    public ClusterResource clusterResource(String engineType, String pluginInfo) throws Exception {
        return clientOperator.getClusterResource(engineType, pluginInfo);
    }
}
