package com.dtstack.taiga.develop.model.system;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.enums.EComponentScheduleType;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.util.Strings;
import com.dtstack.taiga.dao.domain.ScheduleDict;
import com.dtstack.taiga.dao.mapper.DictMapper;
import com.dtstack.taiga.develop.model.exception.InvalidComponentException;
import com.dtstack.taiga.develop.model.system.config.ComponentModel;
import com.dtstack.taiga.develop.model.system.config.ComponentModelExtraParameters;
import com.dtstack.taiga.develop.model.system.config.ComponentModelTypeConfig;
import com.dtstack.taiga.develop.model.system.config.SystemConfigMapperException;
import com.dtstack.taiga.scheduler.enums.DictType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
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
        List<ScheduleDict> dicts = dictMapper.listDictByType(DictType.RESOURCE_MODEL_CONFIG.type);
        return parseComponentConfig(dicts, ComponentModelTypeConfig::new);
    }

    private Map<EComponentType, Map<String, ComponentModelExtraParameters>> initComponentModelExtraParams() {
        List<ScheduleDict> dicts = dictMapper.listDictByType(DictType.EXTRA_VERSION_TEMPLATE.type);
        return parseComponentConfig(dicts, ComponentModelExtraParameters::new);
    }

    private <T> Map<EComponentType, Map<String, T>> parseComponentConfig(List<ScheduleDict> dicts, BiFunction<String, String, T> function) {
        return dicts.stream().collect(Collectors.groupingBy(scheduleDict -> Enum.valueOf(EComponentType.class, scheduleDict.getDependName()),
                Collectors.collectingAndThen(Collectors.toCollection(ArrayList<ScheduleDict>::new),
                        scheduleDicts -> scheduleDicts.stream().collect(Collectors.toMap(ScheduleDict::getDictName,
                                d -> function.apply(d.getDictName(), d.getDictValue()))))));
    }

    private Map<EComponentType, ComponentModel> initComponentModels() {
        List<ScheduleDict> dicts = dictMapper.listDictByType(DictType.COMPONENT_MODEL.type);
        requiredNotEmptyName(dicts, DictType.COMPONENT_MODEL);
        Map<EComponentType, ScheduleDict> typeMap = parseComponentTypes(dicts, DictType.COMPONENT_MODEL);
        return parseComponentModels(typeMap, DictType.COMPONENT_MODEL);
    }

    private Map<EComponentType, ScheduleDict> parseComponentTypes(
            List<ScheduleDict> dicts, DictType type) {
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
            Map<EComponentType, ScheduleDict> dicts, DictType type) {
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
        List<ScheduleDict> dicts = this.dictMapper.listDictByType(DictType.TYPENAME_MAPPING.type);
        requiredNotEmptyName(dicts, DictType.TYPENAME_MAPPING);
        Map<String, Long> res = parseTemplates(dicts, DictType.TYPENAME_MAPPING);
        requiredNegativeValue(res, DictType.TYPENAME_MAPPING);
        return res;
    }

    private void requiredNotEmptyName(List<ScheduleDict> dicts, DictType type) {
        if (dicts.stream().anyMatch(scheduleDict -> StringUtils.isBlank(scheduleDict.getDictName()))) {
            throw new SystemException(Strings.format(
                    "There are empty name in dictionary of type {}",
                    type
            ));
        }
    }


    private Map<String, Long> parseTemplates(List<ScheduleDict> dicts, DictType type) {
        try {
            return dicts.stream().collect(Collectors.toMap(
                    ScheduleDict::getDictName,
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
        if(null == componentModelExtraParametersMap){
            return Optional.empty();
        }
        ComponentModelExtraParameters parameters = componentModelExtraParametersMap.get(resourceVersion);
        if(null == parameters){
            return Optional.empty();
        }
        return Optional.ofNullable(parameters.getComponentModelConfig());
    }
}
