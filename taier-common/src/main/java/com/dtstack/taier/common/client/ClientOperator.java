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

package com.dtstack.taier.common.client;

import com.dtstack.taier.common.exception.ClientAccessException;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.IClient;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Reason:
 * Date: 2018/1/11
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class ClientOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOperator.class);

    private static ClientCache clientCache;

    private static ClientOperator singleton;

    private ClientOperator() {
    }

    public static ClientOperator getInstance(String pluginPath) {
        if (singleton == null) {
            synchronized (ClientOperator.class) {
                if (singleton == null) {
                    clientCache = ClientCache.getInstance(pluginPath);
                    LOGGER.info("init client operator plugin path {}", pluginPath);
                    singleton = new ClientOperator();
                }
            }
        }
        return singleton;
    }

    public TaskStatus getJobStatus(String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(pluginInfo, jobIdentifier);

        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId) && Strings.isNullOrEmpty(jobIdentifier.getApplicationId())) {
            throw new TaierDefineException("can't get job of jobId is empty or null!");
        }

        try {
            IClient client = clientCache.getClient(pluginInfo);
            Object result = client.getJobStatus(jobIdentifier);

            if (result == null) {
                return null;
            }

            return (TaskStatus) result;
        } catch (Exception e) {
            LOGGER.error("getStatus happens error：{}", jobId, e);
            return TaskStatus.NOTFOUND;
        }
    }


    public String getEngineLog(String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(pluginInfo, jobIdentifier);

        String logInfo;
        try {
            IClient client = clientCache.getClient(pluginInfo);
            logInfo = client.getJobLog(jobIdentifier);
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }

        return logInfo;
    }

    public String getCheckpoints(String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(pluginInfo);
            return client.getCheckpoints(jobIdentifier);
        } catch (Exception e) {
            throw new TaierDefineException("get job checkpoints:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }


    public JobResult stopJob(JobClient jobClient) throws Exception {
        if (jobClient.getEngineTaskId() == null && jobClient.getApplicationId() == null) {
            return JobResult.createSuccessResult(jobClient.getJobId());
        }
        JobIdentifier jobIdentifier = new JobIdentifier(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getJobId()
                , jobClient.getTenantId(), jobClient.getTaskType(), jobClient.getDeployMode(), jobClient.getUserId(), jobClient.getPluginInfo(), jobClient.getComponentVersion(), jobClient.getQueueName());
        jobIdentifier.setForceCancel(jobClient.getForceCancel());
        checkoutOperator(jobClient.getPluginInfo(), jobIdentifier);

        jobIdentifier.setTimeout(getCheckoutTimeout(jobClient));
        IClient client = clientCache.getClient(jobClient.getPluginInfo());
        return client.cancelJob(jobIdentifier);
    }

    public Long getCheckoutTimeout(JobClient jobClient) {
        Long timeout = ConfigConstant.DEFAULT_CHECKPOINT_TIMEOUT;
        Properties taskProps = jobClient.getConfProperties();
        if (taskProps == null || taskProps.size() == 0) {
            return timeout;
        }
        if (taskProps.containsKey(ConfigConstant.SQL_CHECKPOINT_TIMEOUT)) {
            timeout = Long.valueOf(taskProps.getProperty(ConfigConstant.SQL_CHECKPOINT_TIMEOUT));
        } else if (taskProps.containsKey(ConfigConstant.FLINK_CHECKPOINT_TIMEOUT)) {
            timeout = Long.valueOf(taskProps.getProperty(ConfigConstant.FLINK_CHECKPOINT_TIMEOUT));
        }
        return timeout;
    }

    private void checkoutOperator(String pluginInfo, JobIdentifier jobIdentifier) {
        if (null == pluginInfo || null == jobIdentifier) {
            throw new IllegalArgumentException("pluginInfo|jobIdentifier is null.");
        }
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getPluginInfo());
        return clusterClient.judgeSlots(jobClient);
    }

    public JobResult submitJob(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getPluginInfo());
        return clusterClient.submitJob(jobClient);
    }

    public List<String> getRollingLogBaseInfo(String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(pluginInfo);
            return client.getRollingLogBaseInfo(jobIdentifier);
        } catch (Exception e) {
            throw new TaierDefineException("get job rollingLogBaseInfo:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public CheckResult grammarCheck(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getPluginInfo());
        return clusterClient.grammarCheck(jobClient);
    }
}
