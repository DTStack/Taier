package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.client.config.AbstractConfigParser;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.google.common.collect.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    private final static Logger logger = LoggerFactory.getLogger(ComponentConfigService.class);
    private final static String dependencySeparator = "$";

    @Autowired
    private ComponentConfigDao componentConfigDao;

    /**
     * 保存页面展示数据
     *
     * @param clientTemplates
     * @param componentId
     * @param clusterId
     * @param engineId
     * @param componentTypeCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateComponentConfig(List<ClientTemplate> clientTemplates, Long componentId, Long clusterId, Long engineId, Integer componentTypeCode) {
        if (null == clusterId || null == componentId || null == componentTypeCode || CollectionUtils.isEmpty(clientTemplates)) {
            throw new RdosDefineException("参数不能为空");
        }
        componentConfigDao.deleteByComponentId(componentId);
        List<ComponentConfig> componentConfigs = saveTreeToDb(clientTemplates, clusterId, engineId, componentId, null, null, componentTypeCode);
        batchSaveComponentConfig(componentConfigs);

    }

    public void deleteComponentConfig(Long componentId) {
        logger.info("delete 【{}】component config ", componentId);
        componentConfigDao.deleteByComponentId(componentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSaveComponentConfig(List<ComponentConfig> saveComponent) {
        if (CollectionUtils.isEmpty(saveComponent)) {
            return;
        }
        List<List<ComponentConfig>> partition = Lists.partition(saveComponent, 50);
        for (List<ComponentConfig> componentConfigs : partition) {
            componentConfigDao.insertBatch(componentConfigs);
        }
    }


    /**
     * 将数据库的信息转换为key-value形式 供插件使用
     * @param componentId
     * @return
     */
    public Map<String,Object> convertComponentConfigToMap(Long componentId){
        List<ComponentConfig> configs = componentConfigDao.listByComponentId(componentId);
        if(CollectionUtils.isEmpty(configs)){
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
        Map<String,Object> configMaps = new HashMap<>(configs.size());
        for (ComponentConfig componentConfig : emptyDependencyValue) {
            Map<String, Object> deepToBuildConfigMap = deepToBuildConfigMap(dependencyMapping, dependencyMapping.size(), componentConfig.getKey());
            if (CollectionUtils.isEmpty(deepToBuildConfigMap)) {
                configMaps.put(componentConfig.getKey(),deepToBuildConfigMap);
            } else {
                configMaps.put(componentConfig.getKey(),componentConfig.getValue());
            }
        }
        return configMaps;
    }

    private Map<String, Object> deepToBuildConfigMap(Map<String, List<ComponentConfig>> dependencyMapping, Integer maxDeep, String key) {
        if (maxDeep <= 0) {
            return new HashMap<>(0);
        }
        List<ComponentConfig> dependValueConfig = dependencyMapping.get(key);
        if (!CollectionUtils.isEmpty(dependValueConfig)) {
            maxDeep = --maxDeep;
            Map<String, Object> dependencyKeyConfigMaps = new HashMap<>();
            for (ComponentConfig componentConfig : dependValueConfig) {
                Map<String, Object> sonConfigMap = deepToBuildConfigMap(dependencyMapping, maxDeep, key + dependencySeparator + componentConfig.getKey());
                if (CollectionUtils.isEmpty(sonConfigMap)) {
                    //key-value 一对一
                    dependencyKeyConfigMaps.put(key, componentConfig.getValue());
                } else {
                    //key-value 一对多
                    dependencyKeyConfigMaps.put(key, sonConfigMap);
                }
            }
            return dependencyKeyConfigMaps;
        }
        return new HashMap<>(0);
    }

    /**
     * 根据组件id返回前端展示结构
     *
     * @param componentId
     * @return
     */
    public List<ClientTemplate> buildDBDataToComponentConfig(Long componentId) {
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(componentId);
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
     * 将数据库数据转换为前端展示的树结构
     *
     * @param dependencyMapping
     * @param maxDeep
     * @param clientTemplate
     * @param dependencyKey
     */
    private void deepToBuildClientTemplate(Map<String, List<ComponentConfig>> dependencyMapping, Integer maxDeep, ClientTemplate clientTemplate, String dependencyKey) {
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
     * 仅在第一次将console_component中component_template 转换为 console_component_config的数据使用
     * component_template旧数据默认最大深度不超过三层
     *
     * @param clientTemplates
     */
    public void deepOldClientTemplate(List<ClientTemplate> clientTemplates, Long componentId, Long clusterId, Long engineId, Integer componentTypeCode) {
        if (null == clusterId || null == componentId || null == componentTypeCode || CollectionUtils.isEmpty(clientTemplates)) {
            throw new RdosDefineException("参数不能为空");
        }
        clientTemplates = convertOldClientTemplateToTree(clientTemplates);
        List<ComponentConfig> componentConfigs = saveTreeToDb(clientTemplates, clusterId, engineId, componentId, null, null, componentTypeCode);
        batchSaveComponentConfig(componentConfigs);
    }


    /**
     * 将数据库console_component的component_template调整为完全的树结构
     *
     * @param clientTemplates
     * @return
     */
    private List<ClientTemplate> convertOldClientTemplateToTree(List<ClientTemplate> clientTemplates) {
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

    /**
     * 将展示的树结构存入数据库中
     *
     * @param reduceTemplate
     * @param clusterId
     * @param engineId
     * @param componentId
     * @param dependKey
     * @param dependValue
     */
    private List<ComponentConfig> saveTreeToDb(List<ClientTemplate> reduceTemplate, Long clusterId, Long engineId, Long componentId, String dependKey, String dependValue, Integer componentTypeCode) {
        List<ComponentConfig> saveComponentConfigs = new ArrayList<>();
        for (ClientTemplate clientTemplate : reduceTemplate) {
            ComponentConfig componentConfig = this.convertClientTemplateToConfig(clientTemplate);
            componentConfig.setEngineId(engineId);
            componentConfig.setClusterId(clusterId);
            componentConfig.setComponentId(componentId);
            componentConfig.setType(Optional.ofNullable(clientTemplate.getType()).orElse(""));
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
                List<ComponentConfig> componentConfigs = saveTreeToDb(clientTemplate.getValues(), clusterId, engineId, componentId,
                        dependKeys, clientTemplate.getDependencyValue(), componentTypeCode);
                if (!CollectionUtils.isEmpty(componentConfigs)) {
                    saveComponentConfigs.addAll(componentConfigs);
                }
            }
        }
        return saveComponentConfigs;
    }

    public ComponentConfig convertClientTemplateToConfig(ClientTemplate clientTemplate) {
        ComponentConfig componentConfig = new ComponentConfig();
        BeanUtils.copyProperties(clientTemplate, componentConfig);
        if (clientTemplate.getValue() instanceof List) {
            componentConfig.setValue(JSONArray.toJSONString(clientTemplate.getValue()));
        } else {
            componentConfig.setValue(null == clientTemplate.getValue() ? "" : String.valueOf(clientTemplate.getValue()));
        }
        return componentConfig;
    }

    private ClientTemplate componentConfigToTemplate(ComponentConfig componentConfig) {
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
