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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.ComponentVersionUtil;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ClusterTenantMapper;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.dao.mapper.ConsoleKerberosMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.*;

@Service
public class ComponentService {

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ConsoleKerberosMapper consoleKerberosMapper;


    public JSONObject wrapperConfig(Long clusterId, Integer componentCode, String componentVersion, Integer deployType) {
        Component component = componentMapper.getByClusterIdAndComponentType(clusterId, componentCode, componentVersion, deployType);
        JSONObject componentConfig = getComponentByClusterId(clusterId, componentCode, false, JSONObject.class, componentVersion);
        JSONObject pluginInfo;
        KerberosConfig kerberosConfig = null;
        if (null != component && StringUtils.isNotBlank(component.getKerberosFileName())) {
            //开启kerberos 添加信息
            kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, componentCode,
                    ComponentVersionUtil.formatMultiVersion(componentCode, componentVersion));
        }
        Map sftpMap = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class, null);
        pluginInfo = wrapperConfig(componentCode, componentConfig.toJSONString(), sftpMap, kerberosConfig);
        return pluginInfo;
    }


    /**
     * 将页面配置参数转换为插件需要的参数
     *
     * @param componentType
     * @param componentConfig
     * @return
     */
    public JSONObject wrapperConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig) {
        EComponentType eComponentType = EComponentType.getByCode(componentType);
        JSONObject dataInfo = new JSONObject();
        if (EComponentType.YARN.equals(eComponentType) || EComponentType.HDFS.equals(eComponentType)) {
            dataInfo.put(eComponentType.getConfName(), JSONObject.parseObject(componentConfig));
        } else {
            dataInfo = JSONObject.parseObject(componentConfig);
        }


        //开启了kerberos
        if (null != kerberosConfig) {
            dataInfo.put(KERBEROS_FILE_TIMESTAMP, kerberosConfig.getGmtModified());
            dataInfo.put(OPEN_KERBEROS, kerberosConfig.getOpenKerberos());
            dataInfo.put(REMOTE_DIR, kerberosConfig.getRemotePath());
            dataInfo.put(PRINCIPAL_FILE, kerberosConfig.getName());
            dataInfo.put(KRB_NAME, kerberosConfig.getKrbName());
            dataInfo.put(PRINCIPAL, kerberosConfig.getPrincipal());
            dataInfo.put(MERGE_KRB5_CONTENT_KEY, kerberosConfig.getMergeKrbContent());
        }
        //common参数
        dataInfo.put(EComponentType.SFTP.getConfName(), sftpConfig);
        return dataInfo;
    }
    public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
        return componentMapper.getByClusterIdAndComponentType(clusterId, componentType, componentVersion, null);
    }

    /**
     * 获取对应组件的配置信息
     *
     * @param clusterId
     * @param componentType
     * @param isFilter      是否移除typeName 等配置信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, String componentVersion, Long componentId) {
        Map<String, Object> configMap = componentConfigService.getCacheComponentConfigMap(clusterId, componentType, isFilter, componentVersion, componentId);
        if (MapUtils.isEmpty(configMap)) {
            return null;
        }
        if (clazz.isInstance(Map.class)) {
            return (T) configMap;
        }
        String configStr = JSONObject.toJSONString(configMap);
        if (clazz.isInstance(String.class)) {
            return (T) configStr;
        }
        return JSONObject.parseObject(configStr, clazz);
    }

    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, String componentVersion) {
        return getComponentByClusterId(clusterId, componentType, isFilter, clazz, componentVersion, null);
    }


    public List<Component> getComponentVersionByEngineType(Long tenantId, Integer taskType) {
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(taskType);
        EComponentType componentType = scheduleJobType.getComponentType();
        List<Component> componentVersionList = componentMapper.getComponentVersionByEngineType(tenantId, componentType.getTypeCode());
        if (CollectionUtils.isEmpty(componentVersionList)) {
            return Collections.emptyList();
        }
        Set<String> distinct = new HashSet<>(2);
        List<Component> components = new ArrayList<>(2);
        for (Component component : componentVersionList) {
            if (distinct.add(component.getVersionValue())) {
                components.add(component);
            }
        }
        return components;
    }

    public List<Component> listComponentsByComponentType(Long tenantId, Integer componentType) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        return componentMapper.listByClusterId(clusterId, componentType, false);
    }

    public List<Component> listAllComponents(Long clusterId) {
        return componentMapper.selectList(Wrappers.lambdaQuery(Component.class).eq(Component::getClusterId, clusterId));
    }

    public List<Component> listAllComponentsByComponent(Long clusterId, List<Integer> componentType) {
        return componentMapper.selectList(Wrappers.lambdaQuery(Component.class).eq(Component::getClusterId, clusterId)
                .in(Component::getComponentTypeCode, componentType));
    }

    public List<Component> listAllByClusterIdAndComponentTypeAndVersionName(Long clusterId, Integer typeCode, String versionName) {
        return componentMapper.selectList(Wrappers.lambdaQuery(Component.class).eq(Component::getClusterId, clusterId)
                .eq(Component::getComponentTypeCode, typeCode)
                .eq(Component::getVersionName, versionName));
    }

    public String buildConfRemoteDir(Long clusterId) {
        Cluster one = clusterMapper.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return "confPath" + File.separator + one.getClusterName();
    }

    public String buildHdfsTypeName(Long tenantId, Long clusterId) {
        if (null == clusterId) {
            clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        }
        Component component = getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(), null);
        if (null == component || StringUtils.isBlank(component.getVersionName())) {
            return "hdfs2";
        }
        String versionName = component.getVersionName();
        List<Dict> dicts = scheduleDictService.listByDictType(DictType.HDFS_TYPE_NAME);
        Optional<Dict> dbTypeNames = dicts.stream().filter(dict -> dict.getDictName().equals(versionName.trim())).findFirst();
        if (dbTypeNames.isPresent()) {
            return dbTypeNames.get().getDictValue();
        }
        String hadoopVersion = component.getVersionValue();
        if (StringUtils.isBlank(hadoopVersion)) {
            return "hdfs2";
        }
        return EComponentType.HDFS.name().toLowerCase() + hadoopVersion.charAt(0);
    }

    public List<Component> listComponents(Long tenantId) {
        return componentMapper.listByTenantId(tenantId);
    }
}
