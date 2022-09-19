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
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ScheduleDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.DEPLOY_MODEL;

@Component
public class PluginWrapper {

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ScheduleDictService scheduleDictService;

    public Map<String, Object> wrapperPluginInfo(Integer taskType, String taskParam, Integer computeType, String componentVersion, Long tenantId, String queueName) {
        EDeployMode deployMode = EDeployMode.PERJOB;
        if (EScheduleJobType.SYNC.getType().equals(taskType)) {
            deployMode = TaskParamsUtils.parseDeployTypeByTaskParams(taskParam, computeType);
        }
        String componentVersionValue = scheduleDictService.convertVersionNameToValue(componentVersion, taskType);
        JSONObject pluginInfo = clusterService.pluginInfoJSON(tenantId, taskType, deployMode.getType(), componentVersionValue, queueName);
        pluginInfo.put(DEPLOY_MODEL, deployMode.getType());
        return pluginInfo;
    }

    public Map<String, Object> wrapperPluginInfo(Integer taskType, String componentVersion, Long tenantId, Integer deployMode, String queueName) {
        String componentVersionValue = scheduleDictService.convertVersionNameToValue(componentVersion, taskType);
        return clusterService.pluginInfoJSON(tenantId, taskType, deployMode, componentVersionValue, queueName);
    }

    public void wrapperJobClient(JobClient jobClient) {
        Map<String, Object> pluginInfo = wrapperPluginInfo(jobClient.getTaskType(), jobClient.getTaskParams(), jobClient.getComputeType().getType(),
                jobClient.getComponentVersion(), jobClient.getTenantId(), jobClient.getQueueName());
        jobClient.setPluginInfo(JSONObject.toJSONString(pluginInfo));
        jobClient.setJobType(EJobType.getEjobType(EScheduleJobType.getByTaskType(jobClient.getTaskType()).getEngineJobType()));
        if (pluginInfo.containsKey(DEPLOY_MODEL)) {
            jobClient.setDeployMode((Integer) pluginInfo.get(DEPLOY_MODEL));
        }
    }
}
