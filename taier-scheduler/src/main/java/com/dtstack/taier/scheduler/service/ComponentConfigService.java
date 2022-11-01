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

import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.dao.mapper.ComponentConfigMapper;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.scheduler.utils.ComponentConfigUtils;
import com.dtstack.taier.scheduler.utils.LocalCacheUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private LocalCacheUtil localCacheUtil;

    private static final String componentCacheGroup = "component";

    /**
     * 保存页面展示数据
     *
     * @param componentId
     * @param clusterId
     * @param componentTypeCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateComponentConfig(Long componentId, Long clusterId, Integer componentTypeCode ,List<ComponentConfig> componentConfigs) {
        if (null == clusterId || null == componentId || null == componentTypeCode || CollectionUtils.isEmpty(componentConfigs)) {
            throw new TaierDefineException("参数不能为空");
        }
        componentConfigMapper.deleteByComponentId(componentId);
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
