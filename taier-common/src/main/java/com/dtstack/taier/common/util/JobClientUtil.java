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

package com.dtstack.taier.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.EQueueSourceType;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


public class JobClientUtil {

    public static JobClient conversionJobClient(ParamAction paramAction) throws IOException {
        JobClient jobClient = new JobClient();
        jobClient.setSql(paramAction.getSqlText());
        jobClient.setTaskParams(paramAction.getTaskParams());
        jobClient.setJobName(paramAction.getName());
        jobClient.setJobId(paramAction.getJobId());
        jobClient.setEngineTaskId(paramAction.getEngineTaskId());
        jobClient.setApplicationId(paramAction.getApplicationId());
        jobClient.setJobType(EJobType.getEjobType(EScheduleJobType.getByTaskType(paramAction.getTaskType()).getEngineJobType()));
        jobClient.setComputeType(ComputeType.getType(paramAction.getComputeType()));
        jobClient.setExternalPath(paramAction.getExternalPath());
        jobClient.setClassArgs(paramAction.getExeArgs());
        jobClient.setGenerateTime(paramAction.getGenerateTime());
        jobClient.setLackingCount(paramAction.getLackingCount());
        jobClient.setTenantId(paramAction.getTenantId());
        jobClient.setQueueSourceType(EQueueSourceType.NORMAL.getCode());
        jobClient.setSubmitExpiredTime(paramAction.getSubmitExpiredTime());
        jobClient.setRetryIntervalTime(paramAction.getRetryIntervalTime());
        jobClient.setComponentVersion(paramAction.getComponentVersion());
        jobClient.setTaskType(paramAction.getTaskType());
        jobClient.setMaxRetryNum(paramAction.getMaxRetryNum() == null ? 0 : paramAction.getMaxRetryNum());
        if (paramAction.getPluginInfo() != null) {
            jobClient.setPluginInfo(JSONObject.toJSONString(paramAction.getPluginInfo()));
        }
        if (jobClient.getTaskParams() != null) {
            jobClient.setConfProperties( PublicUtil.stringToProperties(jobClient.getTaskParams()));
        }
        if (paramAction.getPriority() <= 0) {
            String valStr = jobClient.getConfProperties() == null ? null : jobClient.getConfProperties().getProperty(ConfigConstant.CUSTOMER_PRIORITY_VAL);
            jobClient.setPriorityLevel( valStr == null ? JobClient.DEFAULT_PRIORITY_LEVEL_VALUE : MathUtil.getIntegerVal(valStr));
            //设置priority值, 值越小，优先级越高
            jobClient.setPriority(paramAction.getGenerateTime() + (long)jobClient.getPriorityLevel() * JobClient.PRIORITY_LEVEL_WEIGHT) ;
        } else {
            jobClient.setPriority(paramAction.getPriority());
        }
        String groupName = paramAction.getGroupName();
        if (StringUtils.isBlank(groupName)) {
            groupName = ConfigConstant.DEFAULT_GROUP_NAME;
        }
        jobClient.setGroupName(groupName);
        return jobClient;
    }
}
