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

package com.dtstack.taier.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.ComponentVersionUtil;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.domain.Queue;
import com.dtstack.taier.dao.mapper.*;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.scheduler.server.pluginInfo.*;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.*;
import static java.lang.String.format;

@Service
public class ClusterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private ConsoleQueueMapper consoleQueueMapper;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ConsoleKerberosMapper consoleKerberosMapper;

    public JSONObject pluginInfoJSON(Long tenantId, Integer taskType, Integer deployMode, String componentVersion) {
        EScheduleJobType engineJobType = EScheduleJobType.getByTaskType(taskType);
        EComponentType componentType = engineJobType.getComponentType();
        if (componentType == null) {
            return null;
        }
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null == clusterId) {
            clusterId = DEFAULT_CLUSTER_ID;
        }
        JSONObject clusterConfigJson = buildClusterConfig(clusterId, componentVersion, componentType);
        ComponentPluginInfoStrategy pluginInfoStrategy = convertPluginInfo(componentType);
        KerberosPluginInfo kerberosPluginInfo = new KerberosPluginInfo(pluginInfoStrategy, consoleKerberosMapper, componentMapper);
        JSONObject pluginJson = kerberosPluginInfo.configSecurity(clusterConfigJson, clusterId, deployMode);
        Queue queue = getQueue(tenantId, clusterId);
        pluginJson.put(QUEUE, queue == null ? "" : queue.getQueueName());
        return pluginJson;
    }

    private ComponentPluginInfoStrategy convertPluginInfo(EComponentType componentType) {
        switch (componentType) {
            case FLINK:
                return new FlinkPluginInfoStrategy();
            case SPARK:
                return new SparkPluginInfoStrategy();
            case HIVE_SERVER:
                return new HivePluginInfoStrategy();
            default:
                throw new RdosDefineException(format("The plugin info strategy is not support [%s] component", componentType));
        }
    }




    public Queue getQueue(Long tenantId, Long clusterId) {
        //先获取绑定的
        Long queueId = clusterTenantMapper.getQueueIdByTenantId(tenantId);
        Queue queue = consoleQueueMapper.selectById(queueId);
        if (queue != null) {
            return queue;
        }
        List<Queue> queues = consoleQueueMapper.listByClusterWithLeaf(clusterId);
        if (CollectionUtils.isEmpty(queues)) {
            return null;
        }

        // 没有绑定集群和队列时，返回第一个队列
        return queues.get(0);
    }


    public JSONObject buildClusterConfig(Long clusterId, String componentVersion, EComponentType computeComponentType) {
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        JSONObject config = new JSONObject();
        List<Component> components = componentService.listAllComponents(clusterId);
        for (Component component : components) {
            EComponentType componentType = EComponentType.getByCode(component.getComponentTypeCode());
            if (!EComponentScheduleType.COMPUTE.equals(EComponentType.getScheduleTypeByComponent(component.getComponentTypeCode()))) {
                JSONObject componentConfig = componentService.getComponentByClusterId(clusterId, componentType.getTypeCode(), false, JSONObject.class, null);
                config.put(componentType.getConfName(), componentConfig);
            } else if (componentType.equals(computeComponentType)) {
                JSONObject componentConfig = componentService.getComponentByClusterId(clusterId, componentType.getTypeCode(), false, JSONObject.class, componentVersion);
                config.put(componentType.getConfName(), componentConfig);
            }
            // ignore other compute component
        }
        config.put(CLUSTER, cluster.getClusterName());
        return config;
    }


    public Cluster getCluster(Long clusterId) {
        return clusterMapper.selectById(clusterId);
    }

    public JSONObject getConfigByKey(Long tenantId, String componentConfName, String componentVersion) {
        Long clusterId = Optional.ofNullable(clusterTenantMapper.getClusterIdByTenantId(tenantId)).orElse(DEFAULT_CLUSTER_ID);
        return getConfigByKeyByClusterId(clusterId, componentConfName, componentVersion);
    }

    public JSONObject getConfigByKeyByClusterId(Long clusterId, String componentConfName, String componentVersion) {
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(componentConfName);
        Component component = componentMapper.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), componentVersion, null);
        if (null == component) {
            return null;
        }
        JSONObject configObj = componentService.getComponentByClusterId(clusterId, component.getComponentTypeCode(), false, JSONObject.class, componentVersion);
        if(null == configObj){
            return null;
        }
        if (StringUtils.isNotBlank(component.getKerberosFileName())) {
            //开启kerberos的kerberosFileName不为空
            KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, componentType.getTypeCode(),
                    ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode()) ? StringUtils.isNotBlank(componentVersion) ? componentVersion :
                            componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType.getTypeCode()) : null);
            // 添加组件的kerberos配置信息 应用层使用
            configObj.put(ConfigConstant.KERBEROS_CONFIG,kerberosConfig);
            //填充sftp配置项
            Map sftpMap = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class, null);
            if (MapUtils.isNotEmpty(sftpMap)) {
                configObj.put(EComponentType.SFTP.getConfName(), sftpMap);
            }
        }
        //返回版本
        configObj.put(ConfigConstant.VERSION, component.getVersionValue());
        configObj.put(ConfigConstant.VERSION_NAME, component.getVersionName());
        configObj.put(IS_METADATA, component.getIsMetadata());
        return configObj;
    }

    public <T> T getComponentByTenantId(Long tenantId, Integer componentType, boolean isFilter, Class<T> clazz,String componentVersion) {
        Long clusterId = Optional.ofNullable(clusterTenantMapper.getClusterIdByTenantId(tenantId)).orElse(DEFAULT_CLUSTER_ID);
        return componentService.getComponentByClusterId(clusterId,componentType,isFilter,clazz,componentVersion,null);
    }


    public Boolean hasStandalone(Long tenantId, int typeCode) {
        return false;
    }


    public Integer getMetaComponent(Long tenantId) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        return componentService.getMetaComponentByClusterId(clusterId);
    }
    public Long getClusterIdByTenantId(Long tenantId){
       return clusterTenantMapper.getClusterIdByTenantId(tenantId);
    }

}

