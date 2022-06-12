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
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EFrontType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.mapper.ComponentConfigMapper;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.dao.mapper.DictMapper;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.scheduler.impl.pojo.ClientTemplate;
import com.dtstack.taier.scheduler.utils.ComponentConfigUtils;
import com.dtstack.taier.scheduler.utils.LocalCacheUtil;
import com.dtstack.taier.scheduler.vo.ComponentMultiVersionVO;
import com.dtstack.taier.scheduler.vo.ComponentVO;
import com.dtstack.taier.scheduler.vo.IComponentVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-02-18
 */
@Service
public class ComponentConfigService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentConfigService.class);

    @Autowired
    private ComponentConfigMapper componentConfigMapper;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private LocalCacheUtil localCacheUtil;

    private static final String componentCacheGroup = "component";

    /**
     * 保存页面展示数据
     *
     * @param clientTemplates
     * @param componentId
     * @param clusterId
     * @param componentTypeCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateComponentConfig(List<ClientTemplate> clientTemplates, Long componentId, Long clusterId, Integer componentTypeCode) {
        if (null == clusterId || null == componentId || null == componentTypeCode || CollectionUtils.isEmpty(clientTemplates)) {
            throw new RdosDefineException("参数不能为空");
        }
        componentConfigMapper.deleteByComponentId(componentId);
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, componentId, null, null, componentTypeCode);
        batchSaveComponentConfig(componentConfigs);
    }

    public void deleteComponentConfig(Long componentId) {
        LOGGER.info("delete 【{}】component config ", componentId);
        componentConfigMapper.deleteByComponentId(componentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSaveComponentConfig(List<ComponentConfig> saveComponent) {
        if (CollectionUtils.isEmpty(saveComponent)) {
            return;
        }
        List<List<ComponentConfig>> partition = Lists.partition(saveComponent, 50);
        for (List<ComponentConfig> componentConfigs : partition) {
            componentConfigMapper.insertBatch(componentConfigs);
        }
    }


    public ComponentConfig getComponentConfigByKey(Long componentId,String key) {
        return componentConfigMapper.listByKey(componentId,key);
    }

    public Map<String, Object> convertComponentConfigToMap(Long componentId, boolean isFilter) {
        List<ComponentConfig> componentConfigs = componentConfigMapper.listByComponentId(componentId, isFilter);
        return ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
    }


    public List<IComponentVO> getComponentVoByComponent(List<Component> components, boolean isFilter, Long clusterId, boolean isConvertHadoopVersion, boolean multiVersion) {
        if (null == clusterId) {
            throw new RdosDefineException("集群id不能为空");
        }
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        // 集群所关联的组件的配置
        List<ComponentConfig> componentConfigs = componentConfigMapper.listByClusterId(clusterId, isFilter);
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return new ArrayList<>(0);
        }
        // 组件按类型分组, 因为可能存在组件有多个版本, 此时需要兼容单版本和多版本格式问题
        Map<Integer, IComponentVO> componentVoMap=new HashMap<>(components.size());
        components.stream().collect(Collectors.groupingBy(Component::getComponentTypeCode, Collectors.toList()))
                .forEach((k,v) -> componentVoMap.put(k, multiVersion ?
                        ComponentMultiVersionVO.getInstanceWithCapacityAndType(k, v.size()) : ComponentVO.getInstance() ));
        // 配置按照组件进行分组, 存在组件有多个版本
        Map<Long, List<ComponentConfig>> componentIdConfigs = componentConfigs.stream().collect(Collectors.groupingBy(ComponentConfig::getComponentId));
        List<IComponentVO> componentVoList = new ArrayList<>(components.size());
        for (Component component : components) {
            IComponentVO customComponent = componentVoMap.get(component.getComponentTypeCode());
            ComponentVO componentVO = IComponentVO.getComponentVo(customComponent,component);;
            // 当前组件的配置
            List<ComponentConfig> configs = componentIdConfigs.get(component.getId());
            // hdfs yarn 才将自定义参数移除 过滤返回给前端
            boolean isHadoopControl = EComponentType.hadoopVersionComponents.contains(EComponentType.getByCode(component.getComponentTypeCode()));
            if (isHadoopControl) {
                // 配置按照编辑类型进行分组
                Map<String, List<ComponentConfig>> configTypeMapping = configs.stream().collect(Collectors.groupingBy(ComponentConfig::getType));
                //hdfs yarn 4.1 template只有自定义参数
                componentVO.setComponentTemplate(JSONObject.toJSONString(ComponentConfigUtils.buildDBDataToClientTemplate(configTypeMapping.get(EFrontType.CUSTOM_CONTROL.name()))));
                //hdfs yarn 4.1 config为xml配置参数
                componentVO.setComponentConfig(JSONObject.toJSONString(ComponentConfigUtils.convertComponentConfigToMap(configTypeMapping.get(EFrontType.XML.name()))));
            } else {
                Map<String, Object> configToMap = ComponentConfigUtils.convertComponentConfigToMap(configs);
                componentVO.setComponentTemplate(JSONObject.toJSONString(ComponentConfigUtils.buildDBDataToClientTemplate(configs)));
                componentVO.setComponentConfig(JSONObject.toJSONString(configToMap));
                componentVO.setDeployType(component.getDeployType());
            }

            if (isConvertHadoopVersion && isHadoopControl) {
                //设置hadoopVersion 的key 如cdh 5.1.x
                ComponentConfig componentConfig = componentConfigMapper.listByKey(component.getId(), ConfigConstant.HADOOP_VERSION);
                if (null != componentConfig) {
                    componentVO.setVersionValue(componentConfig.getValue());
                } else if (StringUtils.isNotBlank(component.getVersionValue())) {
                    //兼容老数据
                    String dependName = "hadoop3".equalsIgnoreCase(component.getVersionValue()) || component.getVersionValue().startsWith("3") ? "Hadoop3" : "Hadoop2";
                    List<Dict> hadoopVersion = dictMapper.getByDependName(DictType.HADOOP_VERSION.type, dependName);
                    if (!CollectionUtils.isEmpty(hadoopVersion)) {
                        componentVO.setVersionValue(hadoopVersion.get(0).getDictName());
                    }
                }
            }
            // 多版本才需要调用
            if (customComponent.multiVersion()){
                customComponent.addComponent(componentVO);
            }
        }
        componentVoList.addAll(componentVoMap.values());
        return componentVoList;
    }

    public void updateValueComponentConfig(ComponentConfig componentConfig) {
        componentConfigMapper.updateValueById(componentConfig);
    }

    public Map<String, Object> getCacheComponentConfigMap(Long clusterId, Integer componentType, boolean isFilter, String componentVersion, Long componentId) {
        String cacheKey = LocalCacheUtil.generateKey(clusterId, componentType, isFilter, componentVersion, componentId);
        Map<String, Object> result = (Map<String, Object>)localCacheUtil.get(componentCacheGroup, cacheKey);
        // 如果缓存中存在，直接返回
        if (MapUtils.isNotEmpty(result)) {
            return result;
        }
        // 缓存中不存在，查询 DB
        result = this.innerGetCacheComponentConfigMap(clusterId, componentType, isFilter, componentVersion, componentId);
        if (result == null) {
            result = Collections.emptyMap();
        }
        // 塞入缓存
        localCacheUtil.put(componentCacheGroup, cacheKey, result, LocalCacheUtil.ONE_WEEK_IN_MS);
        return result;
    }

    public Map<String, Object> innerGetCacheComponentConfigMap(Long clusterId, Integer componentType, boolean isFilter, String componentVersion, Long componentId) {
        if (null != componentId) {
            return convertComponentConfigToMap(componentId, isFilter);
        }
        Component component = componentMapper.getByClusterIdAndComponentType(clusterId, componentType, componentVersion,null);
        if (null == component) {
            return null;
        }
        return convertComponentConfigToMap(component.getId(), isFilter);
    }

    public void clearComponentCache() {
        localCacheUtil.removeGroup(componentCacheGroup);
        LOGGER.info(" clear all component cache ");
    }

    public List<ComponentConfig> listByComponentIds(List<Long> componentIds, boolean excludeCustom) {
        List<ComponentConfig> result = new ArrayList<>();
        for (Long componentId : componentIds) {
            List<ComponentConfig> componentConfigs = componentConfigMapper.listByComponentId(componentId, excludeCustom);
            result.addAll(componentConfigs);
        }
        return result;
    }
}
