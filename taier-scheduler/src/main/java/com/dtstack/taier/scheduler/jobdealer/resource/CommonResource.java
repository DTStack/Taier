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
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ClusterTenantMapper;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/11
 */
@Component
public class CommonResource {

    private static Map<EScheduleJobType, CommonResource> resources = new ConcurrentHashMap<>();

    @Autowired
    protected ClusterMapper clusterMapper;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;

    @Autowired
    protected ClusterTenantMapper clusterTenantMapper;


    public ComputeResourceType newInstance(JobClient jobClient) {
        EScheduleJobType taskType = EScheduleJobType.getByTaskType(jobClient.getTaskType());
        CommonResource resource = resources.computeIfAbsent(taskType, k -> {
            CommonResource commonResource = null;
            switch (taskType) {
                case SYNC:
                case FLINK_SQL:
                case DATA_ACQUISITION:
                    commonResource = new FlinkResource();
                    commonResource.setClusterMapper(clusterMapper);
                    commonResource.setClusterService(clusterService);
                    commonResource.setComponentService(componentService);
                    commonResource.setClusterTenantMapper(clusterTenantMapper);
                    break;
                default:
                    commonResource = this;
                    break;
            }
            return commonResource;
        });

        return resource.getComputeResourceType(jobClient);
    }

    public ComputeResourceType getComputeResourceType(JobClient jobClient) {
        EScheduleJobType taskType = EScheduleJobType.getByTaskType(jobClient.getTaskType());
        switch (taskType) {
            case SPARK_SQL:
            case HIVE_SQL:
                return ComputeResourceType.Yarn;
            case SYNC:
                return ComputeResourceType.FlinkYarnSession;
            default:
                return null;
        }
    }

    public ClusterMapper getClusterMapper() {
        return clusterMapper;
    }

    public void setClusterMapper(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    public ClusterService getClusterService() {
        return clusterService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    public ClusterTenantMapper getClusterTenantMapper() {
        return clusterTenantMapper;
    }

    public void setClusterTenantMapper(ClusterTenantMapper clusterTenantMapper) {
        this.clusterTenantMapper = clusterTenantMapper;
    }
}
