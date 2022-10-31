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
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.mapper.DictMapper;
import com.dtstack.taier.develop.model.exception.InvalidComponentException;
import com.dtstack.taier.develop.model.system.config.ComponentModel;
import com.dtstack.taier.develop.model.system.config.ComponentModelExtraParameters;
import com.dtstack.taier.develop.model.system.config.ComponentModelTypeConfig;
import com.dtstack.taier.develop.model.system.config.SystemConfigMapperException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
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
    private final Map<EComponentType, Map<String, ComponentModelTypeConfig>> componentModelTypeConfig;

    /**
     * 资源组件 - version-额外参数信息
     */
    private final Map<EComponentType, Map<String, ComponentModelExtraParameters>> componentModelExtraParams;

    @Autowired
    public Context(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
        this.baseTemplates = Collections.unmodifiableMap(initBaseTemplates());
        this.componentConfigs = Collections.unmodifiableMap(initComponentModels());
        this.componentModelTypeConfig = Collections.unmodifiableMap(initComponentModelTypeConfig());
        this.componentModelExtraParams = Collections.unmodifiableMap(initComponentModelExtraParams());
    }

    private Map<EComponentType, Map<String, ComponentModelTypeConfig>> initComponentModelTypeConfig() {
        List<Dict> dicts = dictMapper.listDictByType(DictType.RESOURCE_MODEL_CONFIG.type);
        return parseComponentConfig(dicts, ComponentModelTypeConfig::new);
    }

    private Map<EComponentType, Map<String, ComponentModelExtraParameters>> initComponentModelExtraParams() {
        List<Dict> dicts = dictMapper.listDictByType(DictType.EXTRA_VERSION_TEMPLATE.type);
        return parseComponentConfig(dicts, ComponentModelExtraParameters::new);
    }

    private <T> Map<EComponentType, Map<String, T>> parseComponentConfig(List<Dict> dicts, BiFunction<String, String, T> function) {
        return dicts.stream().collect(Collectors.groupingBy(scheduleDict -> Enum.valueOf(EComponentType.class, scheduleDict.getDependName()),
                Collectors.collectingAndThen(Collectors.toCollection(ArrayList<Dict>::new),
                        scheduleDicts -> scheduleDicts.stream().collect(Collectors.toMap(Dict::getDictName,
                                d -> function.apply(d.getDictName(), d.getDictValue()))))));
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
        Map<String, ComponentModelTypeConfig> resourceModelConfigMap = componentModelTypeConfig.get(type);
        if (MapUtils.isEmpty(resourceModelConfigMap)) {
            throw new InvalidComponentException(type, "is not config resource model");
        }
        ComponentModelTypeConfig resourceModelConfig = resourceModelConfigMap.get(versionName);
        if (null == resourceModelConfig || null == resourceModelConfig.getComponentModelConfig()) {
            throw new InvalidComponentException(type, String.format(" version %s is not support resource model", versionName));
        }
        return Optional.ofNullable(resourceModelConfig.getComponentModelConfig());
    }

    public Optional<JSONObject> getModelExtraVersionParameters(EComponentType resourceType, String resourceVersion) {
        Map<String, ComponentModelExtraParameters> componentModelExtraParametersMap = componentModelExtraParams.get(resourceType);
        if (null == componentModelExtraParametersMap) {
            return Optional.empty();
        }
        ComponentModelExtraParameters parameters = componentModelExtraParametersMap.get(resourceVersion);
        if (null == parameters) {
            return Optional.empty();
        }
        return Optional.ofNullable(parameters.getComponentModelConfig());
    }
}
