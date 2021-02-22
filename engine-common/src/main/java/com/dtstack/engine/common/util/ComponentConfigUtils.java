package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONArray;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.client.config.AbstractConfigParser;
import com.dtstack.engine.common.enums.EFrontType;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-02-22
 */
public class ComponentConfigUtils {

    private final static String DEPLOY_MODE = "deploymode";
    private final static String dependencySeparator = "$";
    private static Predicate<String> isOtherControl = s -> "typeName".equalsIgnoreCase(s) || "md5Key".equalsIgnoreCase(s);


    /**
     * 将数据库数据转换为前端展示的树结构
     *
     * @param componentConfigs
     * @return
     */
    public static List<ClientTemplate> buildDBDataToClientTemplate(List<ComponentConfig> componentConfigs) {
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return new ArrayList<>(0);
        }
        Map<String, List<ComponentConfig>> dependencyMapping = componentConfigs
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getDependencyKey()))
                .collect(Collectors.groupingBy(ComponentConfig::getDependencyKey));
        List<ClientTemplate> reduceTemplate = new ArrayList<>();
        List<ComponentConfig> emptyDependencyValue = componentConfigs
                .stream()
                .filter(c -> StringUtils.isBlank(c.getDependencyKey()))
                .collect(Collectors.toList());
        for (ComponentConfig componentConfig : emptyDependencyValue) {
            ClientTemplate clientTemplate = componentConfigToTemplate(componentConfig);
            deepToBuildClientTemplate(dependencyMapping, dependencyMapping.size(), clientTemplate, clientTemplate.getKey());
            reduceTemplate.add(clientTemplate);
        }
        return AbstractConfigParser.sortByKey(reduceTemplate);
    }


    /**
     * 解析config成clientTemplate树结构
     *
     * @param dependencyMapping
     * @param maxDeep
     * @param clientTemplate
     * @param dependencyKey
     */
    private static void deepToBuildClientTemplate(Map<String, List<ComponentConfig>> dependencyMapping, Integer maxDeep, ClientTemplate clientTemplate, String dependencyKey) {
        if (maxDeep <= 0) {
            return;
        }
        List<ComponentConfig> dependValueConfig = dependencyMapping.get(dependencyKey);
        if (!CollectionUtils.isEmpty(dependValueConfig)) {
            maxDeep = --maxDeep;
            List<ClientTemplate> valuesTemplate = new ArrayList<>();
            for (ComponentConfig componentConfig : dependValueConfig) {
                ClientTemplate componentClientTemplate = componentConfigToTemplate(componentConfig);
                componentClientTemplate.setRequired(BooleanUtils.toBoolean(componentConfig.getRequired()));
                deepToBuildClientTemplate(dependencyMapping, maxDeep, componentClientTemplate, dependencyKey + dependencySeparator + componentClientTemplate.getKey());
                valuesTemplate.add(componentClientTemplate);
            }
            clientTemplate.setValues(valuesTemplate);
        }

    }


    /**
     * 将数据库数据转换为key-value形式 供插件使用
     * @param configs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertComponentConfigToMap(List<ComponentConfig> configs) {
        Map<String, List<ComponentConfig>> dependencyMapping = configs
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getDependencyKey()))
                .collect(Collectors.groupingBy(ComponentConfig::getDependencyKey));
        List<ComponentConfig> emptyDependencyValue = configs
                .stream()
                .filter(c -> StringUtils.isBlank(c.getDependencyKey()))
                .collect(Collectors.toList());
        Map<String, Object> configMaps = new HashMap<>(configs.size());
        for (ComponentConfig componentConfig : emptyDependencyValue) {
            Map<String, Object> deepToBuildConfigMap = ComponentConfigUtils.deepToBuildConfigMap(dependencyMapping, dependencyMapping.size(), componentConfig.getKey());
            if (DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey())) {
                //特殊处理 离线有用到配置信息 需要保持原来结构
                /*{
                    "deploymode":["perjob"],
                    "perjob":{},
                    "session":{},
                    "typeName":"yarn2-hdfs2-spark210"
                }*/
                Object deployMode = deepToBuildConfigMap.get(DEPLOY_MODE);
                if (deployMode instanceof Map) {
                    configMaps.putAll((Map<? extends String, ?>) deployMode);
                } else {
                    configMaps.putAll(deepToBuildConfigMap);
                }
                configMaps.put(componentConfig.getKey(), JSONArray.parseArray(componentConfig.getValue()));
            } else {
                if (!CollectionUtils.isEmpty(deepToBuildConfigMap)) {
                    configMaps.putAll(deepToBuildConfigMap);
                    if (EFrontType.RADIO.name().equalsIgnoreCase(componentConfig.getType())) {
                        //radio 需要将子配置添加进去 自身也需要
                        configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                    }
                } else {
                    configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                }
            }
        }
        return configMaps;
    }


    /**
     * 将前端展示模板templates转换为key-value形式 供插件使用
     *
     * @param templates
     * @return
     */
    public static Map<String, Object> convertClientTemplateToMap(List<ClientTemplate> templates) {
        if (CollectionUtils.isEmpty(templates)) {
            return new HashMap<>(0);
        }
        List<ComponentConfig> configs = saveTreeToList(templates, null, null, null, null, null, null);
        return convertComponentConfigToMap(configs);
    }


    /**
     * 将展示的树结构转换为List存入数据库中
     *
     * @param reduceTemplate
     * @param clusterId
     * @param engineId
     * @param componentId
     * @param dependKey
     * @param dependValue
     */
    public static List<ComponentConfig> saveTreeToList(List<ClientTemplate> reduceTemplate, Long clusterId, Long
            engineId, Long componentId, String dependKey, String dependValue, Integer componentTypeCode) {
        List<ComponentConfig> saveComponentConfigs = new ArrayList<>();
        for (ClientTemplate clientTemplate : reduceTemplate) {
            ComponentConfig componentConfig = ComponentConfigUtils.convertClientTemplateToConfig(clientTemplate);
            componentConfig.setEngineId(engineId);
            componentConfig.setClusterId(clusterId);
            componentConfig.setComponentId(componentId);
            if (isOtherControl.test(componentConfig.getKey())) {
                componentConfig.setType(EFrontType.OTHER.name());
            } else {
                componentConfig.setType(Optional.ofNullable(clientTemplate.getType()).orElse(""));
            }
            if (StringUtils.isNotBlank(dependKey)) {
                componentConfig.setDependencyKey(dependKey);
            }
            if (StringUtils.isNotBlank(dependValue) && componentConfig.getType().equalsIgnoreCase(EFrontType.GROUP.name())) {
                componentConfig.setDependencyValue(dependValue);
            }

            componentConfig.setRequired(BooleanUtils.toInteger(clientTemplate.isRequired(), 1, 0, 0));
            componentConfig.setComponentTypeCode(componentTypeCode);

            saveComponentConfigs.add(componentConfig);
            if (!CollectionUtils.isEmpty(clientTemplate.getValues())) {
                String dependKeys = "";
                //拼接前缀 用以区分不同group下同名key的情况
                //deploymode$session$metrics.reporter.promgateway.deleteOnShutdown
                //deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown
                if (StringUtils.isNotBlank(dependKey)) {
                    dependKeys = dependKey + dependencySeparator + clientTemplate.getKey();
                } else {
                    dependKeys = clientTemplate.getKey();
                }
                List<ComponentConfig> componentConfigs = saveTreeToList(clientTemplate.getValues(), clusterId, engineId, componentId,
                        dependKeys, clientTemplate.getDependencyValue(), componentTypeCode);
                if (!CollectionUtils.isEmpty(componentConfigs)) {
                    saveComponentConfigs.addAll(componentConfigs);
                }
            }
        }
        return saveComponentConfigs;
    }


    private static Map<String, Object> deepToBuildConfigMap(Map<String, List<ComponentConfig>> dependencyMapping, Integer maxDeep, String key) {
        if (maxDeep <= 0) {
            return new HashMap<>(0);
        }
        List<ComponentConfig> dependValueConfig = dependencyMapping.get(key);
        if (!CollectionUtils.isEmpty(dependValueConfig)) {
            maxDeep = --maxDeep;
            Map<String, Object> keyValuesConfigMaps = new HashMap<>();
            Map<String, Object> oneToMoreConfigMaps = new HashMap<>();
            for (ComponentConfig componentConfig : dependValueConfig) {
                //INPUT为单选
                if (EFrontType.INPUT.name().equalsIgnoreCase(componentConfig.getType()) || EFrontType.SELECT.name().equalsIgnoreCase(componentConfig.getType())) {
                    //key-value 一对一
                    keyValuesConfigMaps.put(componentConfig.getKey(), componentConfig.getValue());
                } else {
                    Map<String, Object> sonConfigMap = deepToBuildConfigMap(dependencyMapping, maxDeep, key + dependencySeparator + componentConfig.getKey());
                    if (!CollectionUtils.isEmpty(sonConfigMap)) {
                        //key-value 一对多
                        oneToMoreConfigMaps.put(componentConfig.getKey(), sonConfigMap);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(oneToMoreConfigMaps)) {
                keyValuesConfigMaps.put(key, oneToMoreConfigMaps);
            }
            return keyValuesConfigMaps;
        }
        return new HashMap<>(0);
    }


    /**
     * 将数据库console_component的component_template调整为完全的树结构
     *
     * @param clientTemplates
     * @return
     */
    @Deprecated
    public static List<ClientTemplate> convertOldClientTemplateToTree(List<ClientTemplate> clientTemplates) {
        //子key
        Map<String, List<ClientTemplate>> dependMapping = clientTemplates
                .stream()
                .filter(c -> StringUtils.isNotBlank(c.getDependencyKey()))
                .collect(Collectors.groupingBy(ClientTemplate::getDependencyKey));
        List<ClientTemplate> reduceTemplate = new ArrayList<>();
        for (ClientTemplate clientTemplate : clientTemplates) {
            if (StringUtils.isBlank(clientTemplate.getDependencyKey())) {
                reduceTemplate.add(clientTemplate);
            }
        }
        for (ClientTemplate clientTemplate : reduceTemplate) {
            List<ClientTemplate> sonTemplate = dependMapping.get(clientTemplate.getKey());
            if (CollectionUtils.isEmpty(sonTemplate)) {
                continue;
            }
            for (ClientTemplate template : sonTemplate) {
                if (null != template) {
                    List<ClientTemplate> values = clientTemplate.getValues();
                    if (null == values) {
                        clientTemplate.setValues(new ArrayList<>());
                    } else {
                        clientTemplate.getValues().removeIf(s -> s.getKey().equalsIgnoreCase(template.getKey()));
                    }
                    clientTemplate.getValues().add(template);
                }
            }
        }
        return reduceTemplate;
    }

    public static ComponentConfig convertClientTemplateToConfig(ClientTemplate clientTemplate) {
        ComponentConfig componentConfig = new ComponentConfig();
        BeanUtils.copyProperties(clientTemplate, componentConfig);
        if (clientTemplate.getValue() instanceof List) {
            componentConfig.setValue(JSONArray.toJSONString(clientTemplate.getValue()));
        } else {
            componentConfig.setValue(null == clientTemplate.getValue() ? "" : String.valueOf(clientTemplate.getValue()));
        }
        return componentConfig;
    }

    private static ClientTemplate componentConfigToTemplate(ComponentConfig componentConfig) {
        ClientTemplate clientTemplate = new ClientTemplate();
        BeanUtils.copyProperties(componentConfig, clientTemplate);
        if (componentConfig.getValue().startsWith("[")) {
            clientTemplate.setValue(JSONArray.parseArray(componentConfig.getValue()));
        } else {
            clientTemplate.setValue(componentConfig.getValue());
        }
        if (null == clientTemplate.getDependencyValue()) {
            clientTemplate.setDependencyKey(null);
        }
        return clientTemplate;
    }
}
