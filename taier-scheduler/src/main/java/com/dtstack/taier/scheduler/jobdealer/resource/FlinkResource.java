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

package com.dtstack.taier.scheduler.jobdealer.resource;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EDeployType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class FlinkResource extends CommonResource {

    private static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";

    private static Map<Long, EComponentType> resourceComponents = new ConcurrentHashMap<>();

    private static final String SESSION = "session";
    private static final String PER_JOB = "per_job";
    private static final String STANDALONE = "standalone";

    @Override
    public ComputeResourceType getComputeResourceType(JobClient jobClient) {

        long tenantId = jobClient.getTenantId();
        EComponentType componentType = resourceComponents.computeIfAbsent(tenantId,
                e -> getResourceEComponentType(jobClient));

        Properties properties = jobClient.getConfProperties();
        ComputeType computeType = jobClient.getComputeType();
        String modeStr = properties.getProperty(FLINK_TASK_RUN_MODE_KEY);
        if (EComponentType.FLINK.getTypeCode().equals(componentType.getTypeCode())) {
            //flink on standalone
            return ComputeResourceType.FlinkOnStandalone;
        } else if (EComponentType.YARN.getTypeCode().equals(componentType.getTypeCode())) {
            if (StringUtils.isEmpty(modeStr)) {
                if (ComputeType.STREAM == computeType) {
                    return ComputeResourceType.Yarn;
                } else {
                    return ComputeResourceType.FlinkYarnSession;
                }
            }
            if (SESSION.equalsIgnoreCase(modeStr)) {
                return ComputeResourceType.FlinkYarnSession;
            } else if (PER_JOB.equalsIgnoreCase(modeStr)) {
                return ComputeResourceType.Yarn;
            } else if (STANDALONE.equals(modeStr)) {
                return ComputeResourceType.FlinkOnStandalone;
            }
        }

        throw new RdosDefineException("not support mode: " + modeStr);
    }

    public EComponentType getResourceEComponentType(JobClient jobClient) {
        long tenantId = jobClient.getTenantId();

        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        Component flinkComponent = componentService.getComponentByClusterId(clusterId, EComponentType.FLINK.getTypeCode(), null);
        if (null != flinkComponent) {
            if (EDeployType.STANDALONE.getType() == flinkComponent.getDeployType()) {
                return EComponentType.FLINK;
            } else if (EDeployType.YARN.getType() == flinkComponent.getDeployType()) {
                return EComponentType.YARN;
            }
        }
        throw new RdosDefineException("No found resource EComponentType");
    }
}
