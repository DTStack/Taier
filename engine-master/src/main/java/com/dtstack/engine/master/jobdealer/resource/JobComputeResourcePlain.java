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

package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.mapper.ClusterMapper;
import com.dtstack.engine.mapper.ClusterTenantMapper;
import com.dtstack.engine.master.service.ClusterService;
import com.dtstack.engine.pluginapi.JobClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT;
import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.SPLIT;

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
        if (ComputeResourcePlain.EngineTypeClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName();
        } else {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName() + SPLIT + jobClient.getComputeType().name().toLowerCase();
        }

        String type = EScheduleType.TEMP_JOB.getType().equals(jobClient.getType())?jobClient.getType()+"":"";
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


    public String parseClusterFromJobResource(String jobResource) {
        if (StringUtils.isBlank(jobResource)) {
            return "";
        }
        String plainType = environmentContext.getComputeResourcePlain();
        List<String> clusterArray = new ArrayList<>();
        String[] split = jobResource.split(SPLIT);
        if (ComputeResourcePlain.EngineTypeClusterQueueComputeType.name().equalsIgnoreCase(plainType)) {
            //engineType_cluster_queue_computeType_computeResourceType 拼接方式
            for (int i = 1; i < split.length - 3; i++) {
                clusterArray.add(split[i]);
            }
        } else {
            // engineType_cluster_queue_computeResourceType 拼接方式
            for (int i = 1; i < split.length - 2; i++) {
                clusterArray.add(split[i]);
            }
        }
        return String.join(SPLIT, clusterArray);
    }


}
