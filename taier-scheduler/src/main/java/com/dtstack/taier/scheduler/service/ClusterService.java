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
import com.dtstack.taier.common.enums.EDeployType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.ComponentVersionUtil;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ClusterTenantMapper;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.dao.mapper.ConsoleKerberosMapper;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.scheduler.server.pluginInfo.ComponentPluginInfoStrategy;
import com.dtstack.taier.scheduler.server.pluginInfo.DefaultPluginInfoStrategy;
import com.dtstack.taier.scheduler.server.pluginInfo.FlinkPluginInfoStrategy;
import com.dtstack.taier.scheduler.server.pluginInfo.HadoopMRPluginInfoStrategy;
import com.dtstack.taier.scheduler.server.pluginInfo.KerberosPluginInfo;
import com.dtstack.taier.scheduler.server.pluginInfo.ScriptPluginInfoStrategy;
import com.dtstack.taier.scheduler.server.pluginInfo.SparkPluginInfoStrategy;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.CLUSTER;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.DEFAULT_CLUSTER_ID;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.QUEUE;

@Service
public class ClusterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    @Autowired
    private ClusterMapper clusterMapper;


    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ConsoleKerberosMapper consoleKerberosMapper;

    public JSONObject pluginInfoJSON(Long tenantId, Integer taskType, Integer deployMode, String componentVersion, String queueName) {
        EScheduleJobType engineJobType = EScheduleJobType.getByTaskType(taskType);
        EComponentType componentType = engineJobType.getComponentType();
        if (componentType == null) {
            return null;
        }
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null == clusterId) {
            clusterId = DEFAULT_CLUSTER_ID;
        }
        JSONObject clusterConfigJson = buildClusterConfig(clusterId, componentVersion, componentType, deployMode);
        ComponentPluginInfoStrategy pluginInfoStrategy = convertPluginInfo(componentType);
        pluginInfoStrategy.setJobType(engineJobType);
        KerberosPluginInfo kerberosPluginInfo = new KerberosPluginInfo(pluginInfoStrategy, consoleKerberosMapper, componentMapper);
        JSONObject pluginJson = kerberosPluginInfo.configSecurity(clusterConfigJson, clusterId, deployMode);
        if (StringUtils.isBlank(queueName)) {
            queueName = clusterTenantMapper.getQueueNameByTenantId(tenantId);
        }
        pluginJson.put(QUEUE, queueName);
        return pluginJson;
    }

    private ComponentPluginInfoStrategy convertPluginInfo(EComponentType componentType) {
        switch (componentType) {
            case FLINK:
                return new FlinkPluginInfoStrategy();
            case SPARK:
                return new SparkPluginInfoStrategy();
            case SCRIPT:
                return new ScriptPluginInfoStrategy();
            case HDFS:
                return new HadoopMRPluginInfoStrategy();
            default:
                return new DefaultPluginInfoStrategy(componentType);
        }
    }


    public JSONObject buildClusterConfig(Long clusterId, String componentVersion, EComponentType computeComponentType, Integer deployMode) {
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            throw new TaierDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        JSONObject config = new JSONObject();
        List<Component> components = componentService.listAllComponents(clusterId);
        EDeployType deployType = EDeployType.convertToDeployType(deployMode);
        for (Component component : components) {
            EComponentType componentType = EComponentType.getByCode(component.getComponentTypeCode());
            if (!EComponentScheduleType.COMPUTE.equals(EComponentType.getByCode(component.getComponentTypeCode()).getComponentScheduleType())) {
                JSONObject componentConfig = componentService.getComponentByClusterId(clusterId, componentType.getTypeCode(), false, JSONObject.class, null);
                config.put(componentType.getConfName(), componentConfig);
            } else if (componentType.equals(computeComponentType)) {
                if (deployType.getType().equals(component.getDeployType()) ||
                        EDeployType.YARN.getType().equals(deployType.getType()) && null == component.getDeployType()) {
                    JSONObject componentConfig = componentService.getComponentByClusterId(clusterId, componentType.getTypeCode(), false, JSONObject.class, componentVersion, component.getId());
                    config.put(componentType.getConfName(), componentConfig);
                }
            }
            // ignore other compute component
        }
        config.put(CLUSTER, cluster.getClusterName());
        return config;
    }


    public Cluster getCluster(Long clusterId) {
        return clusterMapper.selectById(clusterId);
    }

    /**
     * 获取集群SFTP配置信息
     *
     * @param tenantId 租户 id
     * @return sftp 配置
     */
    public Map<String, String> getSftp(Long tenantId) {
        if (Objects.isNull(tenantId)) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        // 解析SFTP配置信息
        JSONObject sftpConfig = getConfigByKey(tenantId, EComponentType.SFTP.getConfName(), null);
        if (Objects.isNull(sftpConfig)) {
            throw new TaierDefineException(ErrorCode.CAN_NOT_FIND_SFTP);
        } else {
            for (String key : sftpConfig.keySet()) {
                map.put(key, sftpConfig.getString(key));
            }
        }
        return map;
    }

    public JSONObject getConfigByKey(Long tenantId, String componentConfName, String componentVersion) {
        Long clusterId = Optional.ofNullable(clusterTenantMapper.getClusterIdByTenantId(tenantId)).orElse(DEFAULT_CLUSTER_ID);
        return getConfigByKeyByClusterId(clusterId, componentConfName, componentVersion, null);
    }

    public JSONObject getConfigByKey(Long tenantId, String componentConfName, String componentVersion, EDeployType deployType) {
        Long clusterId = Optional.ofNullable(clusterTenantMapper.getClusterIdByTenantId(tenantId)).orElse(DEFAULT_CLUSTER_ID);
        return getConfigByKeyByClusterId(clusterId, componentConfName, componentVersion, deployType);
    }

    public JSONObject getConfigByKeyByClusterId(Long clusterId, String componentConfName, String componentVersion, EDeployType deployType) {
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(componentConfName);
        Component component = componentMapper.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), componentVersion, null == deployType ? null : deployType.getType());
        if (null == component) {
            return null;
        }
        JSONObject configObj = componentService.getComponentByClusterId(clusterId, component.getComponentTypeCode(), false, JSONObject.class, componentVersion);
        if (null == configObj) {
            return null;
        }
        if (StringUtils.isNotBlank(component.getKerberosFileName())) {
            //开启kerberos的kerberosFileName不为空
            KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, componentType.getTypeCode(),
                    ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode()) ? StringUtils.isNotBlank(componentVersion) ? componentVersion :
                            componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType.getTypeCode()) : null);
            // 添加组件的kerberos配置信息 应用层使用
            configObj.put(ConfigConstant.KERBEROS_CONFIG, kerberosConfig);
            //填充sftp配置项
            Map sftpMap = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class, null);
            if (MapUtils.isNotEmpty(sftpMap)) {
                configObj.put(EComponentType.SFTP.getConfName(), sftpMap);
            }
        }
        //返回版本
        configObj.put(ConfigConstant.VERSION, component.getVersionValue());
        configObj.put(ConfigConstant.VERSION_NAME, component.getVersionName());
        configObj.put(ConfigConstant.DATA_SOURCE_TYPE, component.getDatasourceType());
        return configObj;
    }

    public <T> T getComponentByTenantId(Long tenantId, Integer componentType, boolean isFilter, Class<T> clazz, String componentVersion) {
        Long clusterId = Optional.ofNullable(clusterTenantMapper.getClusterIdByTenantId(tenantId)).orElse(DEFAULT_CLUSTER_ID);
        return componentService.getComponentByClusterId(clusterId, componentType, isFilter, clazz, componentVersion, null);
    }


    public Boolean hasStandalone(Long tenantId, int typeCode) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null != clusterId) {
            return null != componentMapper.getByClusterIdAndComponentType(clusterId, typeCode, null, EDeployType.STANDALONE.getType());
        }
        return false;
    }

    public Long getClusterIdByTenantId(Long tenantId) {
        return clusterTenantMapper.getClusterIdByTenantId(tenantId);
    }

    public boolean onlyStandaloneType(Long tenantId, EComponentType componentType) {
        if (null == componentType) {
            return false;
        }
        List<Component> components = componentService.listComponentsByComponentType(tenantId, componentType.getTypeCode());
        return components.size() == 1 && EDeployType.STANDALONE.getType().equals(components.get(0).getDeployType());
    }
}

