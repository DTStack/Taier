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

package com.dtstack.taiga.scheduler.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.enums.EFrontType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.ComponentConfig;
import com.dtstack.taiga.pluginapi.sftp.SftpConfig;
import com.dtstack.taiga.pluginapi.util.PublicUtil;
import com.dtstack.taiga.scheduler.impl.pojo.ClientTemplate;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
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
    private static Predicate<ClientTemplate> isAuth = c -> (c.getKey().equalsIgnoreCase("password") || c.getKey().equalsIgnoreCase("rsaPath"))
            && "auth".equalsIgnoreCase(c.getDependencyKey());



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
        return sortByKey(reduceTemplate);
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
                    if(DEPLOY_MODE.equalsIgnoreCase(componentConfig.getKey())){
                        //只设置对应的值
                        JSONArray deployValues = JSONArray.parseArray(componentConfig.getValue());
                        for (Object deployValue : deployValues) {
                            Map deployValueMap = new HashMap<>();
                            deployValueMap.put(deployValue,((Map<?, ?>) specialDeepConfig).get(deployValue));
                            configMaps.putAll(deployValueMap);
                        }
                    }

                } else if (EFrontType.GROUP.name().equalsIgnoreCase(componentConfig.getType())) {
                    //group正常处理
//                    {
//                        "pythonConf":{},
//                        "jupyterConf":{},
//                        "typeName":"yarn2-hdfs2-dtscript",
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
                    } else if (EFrontType.SELECT.name().equalsIgnoreCase(componentConfig.getType())){
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
        //radio联动的值
        Map<String, Map> radioLinkageValues = (Map) deepToBuildConfigMap.get(componentConfig.getKey());
        //radio联动的控件
        List<ComponentConfig> radioLinkageComponentConfigValue = dependencyMapping.get(componentConfig.getKey());
        if (!CollectionUtils.isEmpty(radioLinkageComponentConfigValue)) {
            Optional<ComponentConfig> first = radioLinkageComponentConfigValue
                    .stream()
                    .filter(r -> r.getValue().equalsIgnoreCase(componentConfig.getValue()))
                    .findFirst();
            if (first.isPresent()) {
                //根据选择的控件 选择对应的值 没有选择的值不能设置 否则前端测试联通性判断会出现key不一致
                Map map = radioLinkageValues.get(first.get().getKey());
                if (MapUtils.isNotEmpty(map)) {
                    configMaps.putAll(map);
                }
            }
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


    /**
     * 将数据库console_component的component_template调整为完全的树结构
     *
     * @param clientTemplates
     * @return
     */
    @Deprecated
    public static List<ClientTemplate> convertOldClientTemplateToTree(List<ClientTemplate> clientTemplates) {
        if (CollectionUtils.isEmpty(clientTemplates)) {
            return new ArrayList<>(0);
        }
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

    /**
     * 将yarn hdfs 等xml配置信息转换为clientTemplate
     *
     * @param componentConfigString
     * @return
     */
    public static List<ClientTemplate> convertXMLConfigToComponentConfig(String componentConfigString) {
        if (StringUtils.isBlank(componentConfigString)) {
            return new ArrayList<>(0);
        }
        JSONObject componentConfigObj = JSONObject.parseObject(componentConfigString);
        List<ClientTemplate> configs = new ArrayList<>(componentConfigObj.size());
        for (String key : componentConfigObj.keySet()) {
            configs.add(buildCustom(key, componentConfigObj.get(key), EFrontType.XML.name()));
        }
        return configs;
    }


    public static void fillClientTemplate(ClientTemplate clientTemplate, JSONObject config) {
        String value = config.getString(clientTemplate.getKey());
        if (StringUtils.isNotBlank(value)) {
            if(!isAuth.test(clientTemplate)){
                clientTemplate.setValue(value);
            }
            if (!CollectionUtils.isEmpty(clientTemplate.getValues())) {
                for (ClientTemplate clientTemplateValue : clientTemplate.getValues()) {
                    fillClientTemplate(clientTemplateValue, config);
                }
            }
        }
    }

    public static ClientTemplate buildOthers(String key, String value) {
        return buildCustom(key, value, EFrontType.OTHER.name());
    }

    public static ClientTemplate buildCustom(String key, Object value, String type) {
        ClientTemplate componentConfig = new ClientTemplate();
        componentConfig.setType(type);
        componentConfig.setKey(key);
        componentConfig.setValue(value);
        return componentConfig;
    }

    /**
     * 根据key值来排序
     *
     * @param clientTemplates
     * @return
     */
    public static List<ClientTemplate> sortByKey(List<ClientTemplate> clientTemplates) {
        if (CollectionUtils.isEmpty(clientTemplates)) {
            return clientTemplates;
        }
        clientTemplates.sort(Comparator.nullsFirst(Comparator.comparing(ClientTemplate::getKey, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))));
        for (ClientTemplate clientTemplate : clientTemplates) {
            ComponentConfigUtils.sortByKey(clientTemplate.getValues());
        }
        return clientTemplates;
    }


    /**
     * 处理hdfs 和yarn的自定义参数
     *
     * @param componentType
     * @param componentTemplate
     * @return
     */
    public static List<ClientTemplate> dealXmlCustomControl(EComponentType componentType, String componentTemplate) {
        List<ClientTemplate> extraClient = new ArrayList<>(0);
        if (StringUtils.isBlank(componentTemplate)) {
            return extraClient;
        }
        if (EComponentType.HDFS.getTypeCode().equals(componentType.getTypeCode()) || EComponentType.YARN.getTypeCode().equals(componentType.getTypeCode())) {
            JSONArray keyValues = JSONObject.parseArray(componentTemplate);
            for (int i = 0; i < keyValues.size(); i++) {
                ClientTemplate clientTemplate = ComponentConfigUtils.buildCustom(
                        keyValues.getJSONObject(i).getString("key"),
                        keyValues.getJSONObject(i).getString("value"),
                        EFrontType.CUSTOM_CONTROL.name());
                extraClient.add(clientTemplate);
            }
        }
        return extraClient;
    }
}
