package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-02-18
 */
@Service
public class ComponentConfigService {

    private final static Logger logger = LoggerFactory.getLogger(ComponentConfigService.class);

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
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, engineId, componentId, null, null, componentTypeCode);
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
     * 将yarn hdfs 等xml配置信息转换为clientTemplate
     *
     * @param componentConfigString
     * @return
     */
    public List<ClientTemplate> convertXMLConfigToComponentConfig(String componentConfigString) {
        if (StringUtils.isBlank(componentConfigString)) {
            return new ArrayList<>(0);
        }
        JSONObject componentConfigObj = JSONObject.parseObject(componentConfigString);
        List<ClientTemplate> configs = new ArrayList<>(componentConfigObj.size());
        for (String key : componentConfigObj.keySet()) {
            ClientTemplate componentConfig = new ClientTemplate();
            componentConfig.setType(EFrontType.XML.name());
            componentConfig.setKey(key);
            componentConfig.setValue(componentConfigObj.get(key));
            configs.add(componentConfig);
        }
        return configs;
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
        clientTemplates = ComponentConfigUtils.convertOldClientTemplateToTree(clientTemplates);
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, engineId, componentId, null, null, componentTypeCode);
        batchSaveComponentConfig(componentConfigs);
    }


    public Map<String, Object> convertComponentConfigToMap(Long componentId,boolean isFilter) {
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(componentId,isFilter);
        return ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
    }

}
