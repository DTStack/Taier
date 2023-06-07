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

package com.dtstack.taier.develop.model.system;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EFrontType;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.mapper.DictMapper;
import com.dtstack.taier.develop.model.exception.InvalidComponentException;
import com.dtstack.taier.develop.model.system.config.ComponentModel;
import com.dtstack.taier.develop.model.system.config.ComponentModelExtraParameters;
import com.dtstack.taier.develop.model.system.config.ComponentModelTypeConfig;
import com.dtstack.taier.develop.model.system.config.SystemConfigMapperException;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 组件依赖以及组件模版上下文
 */
@Component
public class Context {

    private final DictMapper dictMapper;

    /**
     * 组件模版pluginName - component_config 对应信息
     */
    private final Map<String, Long> baseTemplates;

    /**
     * 组件 - 组件config配置信息
     */
    private final Map<EComponentType, ComponentModel> componentConfigs;

    /**
     * 资源组件 - version-依赖资源组件typeName
     */
    private final Table<EComponentType, String, ComponentModelTypeConfig> componentModelTypeConfig;

    /**
     * 资源组件 - version-额外参数信息
     */
    private final Table<EComponentType, String, ComponentModelExtraParameters> componentModelExtraParams;

    /**
     * 组件+版本 对应datasourceType关系
     */
    private final Table<Integer, String, Integer> componentDatasourceMapping;

    /**
     * 组件 tip 缓存 - {componentTypeCode, {key, ScheduleDict}}
     */
    private final Table<Integer, String, Dict> tipDictCache;

    @Autowired
    public Context(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
        this.baseTemplates = Collections.unmodifiableMap(initBaseTemplates());
        this.componentConfigs = Collections.unmodifiableMap(initComponentModels());
        this.componentModelTypeConfig = initComponentModelTypeConfig();
        this.componentModelExtraParams = initComponentModelExtraParams();
        this.tipDictCache = initTipDictCache();
        this.componentDatasourceMapping = initComponentDatasourceMapping();
    }

    private Table<Integer, String, Integer> initComponentDatasourceMapping() {
        return parseTable(DictType.COMPONENT_DATASOURCE_MAPPING.type, ((table, dict) ->
                table.put(Integer.valueOf(dict.getDictName()), dict.getDependName(), Integer.valueOf(dict.getDictValue()))));
    }


    private Table<EComponentType, String, ComponentModelTypeConfig> initComponentModelTypeConfig() {
         return parseTable(DictType.RESOURCE_MODEL_CONFIG.type, ((table, dict) ->
                table.put(Enum.valueOf(EComponentType.class, dict.getDependName()), dict.getDictName(), new ComponentModelTypeConfig(dict.getDictName(), dict.getDictValue()))));
    }

    private Table<EComponentType, String, ComponentModelExtraParameters> initComponentModelExtraParams() {
        return parseTable(DictType.EXTRA_VERSION_TEMPLATE.type, ((table, dict) ->
                table.put(Enum.valueOf(EComponentType.class, dict.getDependName()), dict.getDictName(), new ComponentModelExtraParameters(dict.getDictName(), dict.getDictValue()))));
    }

    private Table<Integer, String, Dict> initTipDictCache() {
        return parseTable(DictType.TIPS.type, (table, dict) -> table.put(Integer.valueOf(dict.getDictDesc()), dict.getDictName(), dict));
    }

    private <R, C, V> Table<R, C, V> parseTable(Integer dictType, BiConsumer<Table<R, C, V>, Dict> applyFunc) {
        List<Dict> dicts = dictMapper.listDictByType(dictType);
        Table<R, C, V> table = HashBasedTable.create();
        if (CollectionUtils.isNotEmpty(dicts)) {
            dicts.forEach(dict -> applyFunc.accept(table, dict));
        }
        return table;
    }


    private Map<EComponentType, ComponentModel> initComponentModels() {
        List<Dict> dicts = dictMapper.listDictByType(DictType.COMPONENT_MODEL.type);
        requiredNotEmptyName(dicts, DictType.COMPONENT_MODEL);
        Map<EComponentType, Dict> typeMap = parseComponentTypes(dicts, DictType.COMPONENT_MODEL);
        return parseComponentModels(typeMap, DictType.COMPONENT_MODEL);
    }

    private Map<EComponentType, Dict> parseComponentTypes(
            List<Dict> dicts, DictType type) {
        try {
            return dicts.stream()
                    .collect(Collectors.toMap(
                            d -> EComponentType.valueOf(d.getDictName()),
                            Function.identity()
                    ));
        } catch (IllegalArgumentException e) {
            throw new SystemException(Strings.format(
                    "There are unknown component type name in dictionary of type {}",
                    type
            ), e);
        } catch (IllegalStateException e) {
            throw new SystemException(Strings.format(
                    "There are duplicate name in dictionary of type {}",
                    type
            ), e);
        }
    }

    private Map<EComponentType, ComponentModel> parseComponentModels(
            Map<EComponentType, Dict> dicts, DictType type) {
        try {
            return dicts.keySet().stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            t -> new ComponentModel(t, dicts.get(t), this.dictMapper)
                    ));
        } catch (SystemConfigMapperException e) {
            throw new SystemException(Strings.format(
                    "There are error component model config in dictionary of type {}.",
                    type
            ), e);
        }
    }


    private Map<String, Long> initBaseTemplates() {
        List<Dict> dicts = this.dictMapper.listDictByType(DictType.TYPENAME_MAPPING.type);
        requiredNotEmptyName(dicts, DictType.TYPENAME_MAPPING);
        Map<String, Long> res = parseTemplates(dicts, DictType.TYPENAME_MAPPING);
        requiredNegativeValue(res, DictType.TYPENAME_MAPPING);
        return res;
    }

    private void requiredNotEmptyName(List<Dict> dicts, DictType type) {
        if (dicts.stream().anyMatch(scheduleDict -> StringUtils.isBlank(scheduleDict.getDictName()))) {
            throw new SystemException(Strings.format(
                    "There are empty name in dictionary of type {}",
                    type
            ));
        }
    }


    private Map<String, Long> parseTemplates(List<Dict> dicts, DictType type) {
        try {
            return dicts.stream().collect(Collectors.toMap(
                    Dict::getDictName,
                    d -> Long.parseLong(d.getDictValue())
            ));
        } catch (IllegalStateException e) {
            throw new SystemException(Strings.format(
                    "There are duplicate name in dictionary of type {}",
                    type
            ), e);
        } catch (NumberFormatException e) {
            throw new SystemException(Strings.format(
                    "There are error number format of value in dictionary of type {}",
                    type
            ), e);
        }
    }

    private void requiredNegativeValue(Map<String, Long> map, DictType type) {
        map.forEach((k, v) -> {
            if (v >= 0) {
                throw new SystemException(Strings.format(
                        "There are not negative value in dictionary of type {}",
                        type
                ));
            }
        });
    }

    public EComponentScheduleType getOwner(EComponentType type) {
        Objects.requireNonNull(type, "Type is null.");
        ComponentModel componentModel = getComponentModel(type);
        return componentModel.getOwner();
    }

    public List<EComponentScheduleType> getDependsOn(EComponentType type) {
        Objects.requireNonNull(type, "Type is null");
        return getComponentModel(type).getDependsOn();
    }

    public Optional<Long> getBaseTemplateId(String pluginName) {
        Objects.requireNonNull(pluginName, "pluginName is null.");
        return Optional.ofNullable(this.baseTemplates.get(pluginName));
    }


    public ComponentModel getComponentModel(EComponentType type) {
        ComponentModel config = this.componentConfigs.get(type);
        if (config == null) {
            throw new SystemException(Strings.format("Component model of type {} is not found.", type));
        }
        return config;
    }

    public Optional<JSONObject> getModelConfig(EComponentType type, String versionName) {
        ComponentModelTypeConfig resourceModelConfig = componentModelTypeConfig.get(type, versionName);
        if (null == resourceModelConfig || null == resourceModelConfig.getComponentModelConfig()) {
            throw new InvalidComponentException(type, String.format(" version %s is not support resource model", versionName));
        }
        return Optional.ofNullable(resourceModelConfig.getComponentModelConfig());
    }

    public Optional<JSONObject> getModelExtraVersionParameters(EComponentType resourceType, String resourceVersion) {
        ComponentModelExtraParameters parameters = componentModelExtraParams.get(resourceType, resourceVersion);
        if (null == parameters) {
            return Optional.empty();
        }
        return Optional.ofNullable(parameters.getComponentModelConfig());
    }

    /**
     * 填充 tip
     *
     * @param componentConfigs
     * @param componentType
     */
    public void populateTip(List<ComponentConfig> componentConfigs, Integer componentType) {
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return;
        }
        for (ComponentConfig componentConfig : componentConfigs) {
            Dict dict = this.tipDictCache.get(componentType, componentConfig.getKey());
            if (dict == null) {
                continue;
            }
            if (StringUtils.isEmpty(componentConfig.getType())
                    || EFrontType.CUSTOM_CONTROL.name().equalsIgnoreCase(componentConfig.getType())) {
                continue;
            }
            componentConfig.setKeyDescribe(dict.getDictValue());
        }
    }

    public Integer getDataSourceTypeByComponentAndVersion(Integer componentType, String versionName) {
        return componentDatasourceMapping.get(componentType, versionName);
    }
}
