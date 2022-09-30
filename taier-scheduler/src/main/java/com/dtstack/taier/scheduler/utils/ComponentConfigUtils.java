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

package com.dtstack.taier.scheduler.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EFrontType;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.scheduler.impl.pojo.ClientTemplate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-02-22
 */
public class ComponentConfigUtils {

    public final static String DEPLOY_MODE = "deploymode";
    private final static String dependencySeparator = "$";
    private static Predicate<String> isOtherControl = s -> "typeName".equalsIgnoreCase(s) || "md5Key".equalsIgnoreCase(s);


    /**
     * 将数据库数据转换为key-value形式 供插件使用
     *
     * @param configs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertComponentConfigToMap(List<ComponentConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            return new HashMap<>(0);
        }
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
            if (DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey()) || EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                configMaps.put(componentConfig.getKey(), DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey()) ?
                        JSONArray.parseArray(componentConfig.getValue()) : componentConfig.getValue());
                Object specialDeepConfig = deepToBuildConfigMap.get(componentConfig.getKey());
                if (specialDeepConfig instanceof Map) {
                    // DEPLOY_MODE特殊处理 需要将特殊结构下的value 放到同级下 保持原结构一致
//                    {
//                        "deploymode":[{"perjob":""},{"session":""}],
//                        "typeName":"yarn2-hdfs2-spark210"
//                    }
//                      调整为
//                    {
//                        "deploymode":["perjob"],
//                        "perjob":"",
//                        "session":"",
//                        "typeName":"yarn2-hdfs2-spark210"
//                    }
                    if (DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey())) {
                        //只设置对应的值
                        JSONArray deployValues = JSONArray.parseArray(componentConfig.getValue());
                        for (Object deployValue : deployValues) {
                            Map deployValueMap = new HashMap<>();
                            deployValueMap.put(deployValue, ((Map<?, ?>) specialDeepConfig).get(deployValue));
                            configMaps.putAll(deployValueMap);
                        }
                    }

                } else if (EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                    //group正常处理
//                    {
//                        "pythonConf":{},
//                        "jupyterConf":{},
//                        "typeName":"yarn2-hdfs2-script",
//                        "commonConf":{}
//                    }
                    configMaps.put(componentConfig.getKey(), deepToBuildConfigMap);
                } else {
                    configMaps.putAll(deepToBuildConfigMap);
                }

            } else {
                if (!CollectionUtils.isEmpty(deepToBuildConfigMap)) {
                    if (EFrontType.RADIO_LINKAGE.name().equalsIgnoreCase(componentConfig.getType())) {
                        parseRadioLinkage(dependencyMapping, configMaps, componentConfig, deepToBuildConfigMap);
                    } else if (EFrontType.SELECT.name().equalsIgnoreCase(componentConfig.getType())) {
                        configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                    } else {
                        configMaps.putAll(deepToBuildConfigMap);
                        if (EFrontType.RADIO.name().equalsIgnoreCase(componentConfig.getType())) {
                            //radio 需要将子配置添加进去 自身也需要
                            configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                        }
                    }
                } else {
                    configMaps.put(componentConfig.getKey(), componentConfig.getValue());
                }
            }
        }
        return configMaps;
    }

    private static void parseRadioLinkage(Map<String, List<ComponentConfig>> dependencyMapping, Map<String, Object> configMaps, ComponentConfig componentConfig, Map<String, Object> deepToBuildConfigMap) {
        //radio 联动 需要将设置radio选择的值 并根据radio的指选择values中对于的key value
        configMaps.put(componentConfig.getKey(), componentConfig.getValue());
        //radio联动的控件
        List<ComponentConfig> radioLinkageComponentConfigValue = dependencyMapping.get(componentConfig.getKey());
        if (!CollectionUtils.isEmpty(radioLinkageComponentConfigValue)) {
            radioLinkageComponentConfigValue
                    .stream()
                    .filter(r -> StringUtils.isNotBlank(r.getDependencyValue()) && r.getDependencyValue().equalsIgnoreCase(componentConfig.getValue()))
                    .findFirst().ifPresent(config -> configMaps.put(config.getKey(), config.getValue()));
        }
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
        List<ComponentConfig> configs = saveTreeToList(templates, null, null, null, null, null);
        return convertComponentConfigToMap(configs);
    }


    /**
     * 将展示的树结构转换为List存入数据库中
     *
     * @param reduceTemplate
     * @param clusterId
     * @param componentId
     * @param dependKey
     * @param dependValue
     */
    public static List<ComponentConfig> saveTreeToList(List<ClientTemplate> reduceTemplate, Long clusterId, Long componentId, String dependKey, String dependValue, Integer componentTypeCode) {
        List<ComponentConfig> saveComponentConfigs = new ArrayList<>();
        for (ClientTemplate clientTemplate : reduceTemplate) {
            ComponentConfig componentConfig = ComponentConfigUtils.convertClientTemplateToConfig(clientTemplate);
            componentConfig.setClusterId(clusterId);
            componentConfig.setComponentId(componentId);
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
                List<ComponentConfig> componentConfigs = saveTreeToList(clientTemplate.getValues(), clusterId, componentId,
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
                    } else {
                        //type类型为空
                        keyValuesConfigMaps.put(componentConfig.getKey(), componentConfig.getValue());
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


    public static ComponentConfig convertClientTemplateToConfig(ClientTemplate clientTemplate) {
        ComponentConfig componentConfig = new ComponentConfig();
        BeanUtils.copyProperties(clientTemplate, componentConfig);
        if (clientTemplate.getValue() instanceof List) {
            componentConfig.setValue(JSONArray.toJSONString(clientTemplate.getValue()));
        } else {
            componentConfig.setValue(null == clientTemplate.getValue() ? "" : String.valueOf(clientTemplate.getValue()));
        }
        if (null != componentConfig.getValue()) {
            componentConfig.setValue(componentConfig.getValue().trim());
        }
        if (isOtherControl.test(componentConfig.getKey())) {
            componentConfig.setType(EFrontType.OTHER.name());
        } else if (EFrontType.PASSWORD.name().equalsIgnoreCase(componentConfig.getKey()) && StringUtils.isBlank(componentConfig.getDependencyKey())) {
            //key password的控件转换为加密显示
            componentConfig.setType(EFrontType.PASSWORD.name());
        } else {
            componentConfig.setType(Optional.ofNullable(clientTemplate.getType()).orElse("").toUpperCase());
        }
        return componentConfig;
    }

    private static ClientTemplate componentConfigToTemplate(ComponentConfig componentConfig) {
        ClientTemplate clientTemplate = new ClientTemplate();
        BeanUtils.copyProperties(componentConfig, clientTemplate);
        clientTemplate.setRequired(BooleanUtils.toBoolean(componentConfig.getRequired()));
        if (componentConfig.getValue().startsWith("[")) {
            clientTemplate.setValue(JSONArray.parseArray(componentConfig.getValue()));
        } else {
            if (StringUtils.isBlank(componentConfig.getValue()) && EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                //group key 和value 同值
                clientTemplate.setValue(componentConfig.getKey());
            } else {
                clientTemplate.setValue(componentConfig.getValue());
            }

        }
        if (null == clientTemplate.getDependencyValue()) {
            clientTemplate.setDependencyKey(null);
        }
        return clientTemplate;
    }


    public static ComponentConfig buildOthers(String key, String value, Long componentId, Long clusterId, Integer componentCode) {
        return buildCustomConfig(key, value, EFrontType.OTHER.name(), null, componentId, clusterId, componentCode);
    }

    public static ComponentConfig buildCustomConfig(String key, String value, String type,String dependencyKey,
                                                   Long componentId,Long clusterId,Integer componentCode) {
        ComponentConfig componentConfig = new ComponentConfig();
        componentConfig.setType(type);
        componentConfig.setKey(key);
        componentConfig.setValue(value);
        componentConfig.setDependencyKey(dependencyKey);
        componentConfig.setRequired(BooleanUtils.toInteger(false));
        componentConfig.setComponentId(componentId);
        componentConfig.setClusterId(clusterId);
        componentConfig.setComponentTypeCode(componentCode);
        return componentConfig;
    }


    public static List<ComponentConfig> fillTemplateValue(JSONObject configJson, List<ComponentConfig> templateConfigs, Long componentId, Long clusterId, Integer componentCode) {
        List<ComponentConfig> saveConfig = new ArrayList<>(configJson.size());
        if (configJson.containsKey(DEPLOY_MODE)) {
            //flink spark
            JSONArray deployMode = configJson.getJSONArray(DEPLOY_MODE);
            Map<String, List<ComponentConfig>> dependencyMapping = templateConfigs
                    .stream()
                    .filter(c -> c.getDependencyKey() != null)
                    .collect(Collectors.groupingBy(ComponentConfig::getDependencyKey));

            List<ComponentConfig> componentConfigs = dependencyMapping.get(Strings.EMPTY);
            componentConfigs.forEach(deploy -> {
                if (deploy.getKey().equals(DEPLOY_MODE)) {
                    deploy.setValue(deployMode.toJSONString());
                }
                deploy.setClusterId(clusterId);
                deploy.setComponentId(componentId);
                deploy.setComponentTypeCode(componentCode);
            });

            //设置deployMode值
            saveConfig.addAll(componentConfigs);

            for (int i = 0; i < deployMode.size(); i++) {
                JSONObject deployConfig = configJson.getJSONObject(deployMode.getString(i).trim());
                String deployKey = DEPLOY_MODE + dependencySeparator + deployMode.getString(i).trim();
                List<ComponentConfig> deployTemplateConfig = dependencyMapping.get(deployKey);
                deployTemplateConfig = deployTemplateConfig.stream().peek(componentConfig -> {
                    componentConfig.setValue(deployConfig.getString(componentConfig.getKey()));
                    componentConfig.setComponentId(componentId);
                    componentConfig.setClusterId(clusterId);
                    componentConfig.setComponentTypeCode(componentCode);
                    deployConfig.remove(componentConfig.getKey());
                }).collect(Collectors.toList());

                //设置group
                List<ComponentConfig> deployConfigMode = dependencyMapping.get(DEPLOY_MODE);
                for (ComponentConfig componentConfig : deployConfigMode) {
                    if (deployMode.getString(i).trim().equals(componentConfig.getKey())) {
                        componentConfig.setComponentId(componentId);
                        componentConfig.setClusterId(clusterId);
                        componentConfig.setComponentTypeCode(componentCode);
                        deployTemplateConfig.add(componentConfig);
                    }
                }

                //设置模版值
                saveConfig.addAll(deployTemplateConfig);

                if (!deployConfig.isEmpty()) {
                    //多余自定义参数
                    List<ComponentConfig> customKey = deployConfig.keySet().stream().map(key ->
                            buildCustomConfig(key, deployConfig.getString(key), EFrontType.CUSTOM_CONTROL.name(),
                                    deployKey, componentId,clusterId, componentCode)).collect(Collectors.toList());
                    //设置自定义参数值
                    saveConfig.addAll(customKey);

                }
            }
        } else {
            //平铺的组件配置
            saveConfig = templateConfigs.stream().peek(componentConfig -> {
                componentConfig.setValue(configJson.getString(componentConfig.getKey()));
                componentConfig.setComponentId(componentId);
                componentConfig.setClusterId(clusterId);
                componentConfig.setComponentTypeCode(componentCode);
                configJson.remove(componentConfig.getKey());
            }).collect(Collectors.toList());

            if (!configJson.isEmpty()) {
                //多余自定义参数
                List<ComponentConfig> customKey = configJson.keySet().stream().map(key ->
                        buildCustomConfig(key, configJson.getString(key), EFrontType.CUSTOM_CONTROL.name(),
                                null, clusterId, componentId, componentCode)).collect(Collectors.toList());
                //设置自定义参数值
                saveConfig.addAll(customKey);
            }
        }
        return saveConfig;
    }
}
