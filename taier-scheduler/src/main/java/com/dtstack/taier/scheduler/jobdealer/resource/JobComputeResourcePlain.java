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

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Queue;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ClusterTenantMapper;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.scheduler.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.SPLIT;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class JobComputeResourcePlain {

    @Autowired
    private CommonResource commonResource;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private ClusterService clusterService;


    public String getJobResource(JobClient jobClient) {
        this.buildJobClientGroupName(jobClient);
        ComputeResourceType computeResourceType = commonResource.newInstance(jobClient);

        String plainType = environmentContext.getComputeResourcePlain();
        String jobResource = null;
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(jobClient.getTaskType());
        if (ComputeResourcePlain.EngineTypeClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = scheduleJobType.name() + SPLIT + jobClient.getGroupName();
        } else {
            jobResource = scheduleJobType.name() + SPLIT + jobClient.getGroupName() + SPLIT + jobClient.getComputeType().name().toLowerCase();
        }

        String type = EScheduleType.TEMP_JOB.getType().equals(jobClient.getType()) ? jobClient.getType() + "" : "";
        return jobResource + SPLIT + computeResourceType.name() + type;
    }


    private void buildJobClientGroupName(JobClient jobClient) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(jobClient.getTenantId());
        if (null == clusterId) {
            return;
        }
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            return;
        }
        String clusterName = cluster.getClusterName();
        //%s_default
        String groupName = clusterName + SPLIT + RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT;

        Queue queue = clusterService.getQueue(jobClient.getTenantId(), clusterId);
        if (null != queue) {
            groupName = clusterName + SPLIT + queue.getQueueName();
        }
        jobClient.setGroupName(groupName);
    }


}
