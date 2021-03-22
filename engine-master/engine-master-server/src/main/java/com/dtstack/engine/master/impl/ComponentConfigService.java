package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.EComponentType;
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
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.TYPE_NAME_KEY;

/**
 * @author yuebai
 * @date 2021-02-18
 */
@Service
public class ComponentConfigService {

    private final static Logger logger = LoggerFactory.getLogger(ComponentConfigService.class);

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ScheduleDictDao scheduleDictDao;

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
        componentConfigDao.deleteByComponentId(componentId);
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, componentId, null, null, componentTypeCode);
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
     * 仅在第一次将console_component中component_template 转换为 console_component_config的数据使用
     * component_template旧数据默认最大深度不超过三层
     * typeName必须要从componentConfig获取
     *
     * @param componentConfig
     * @param componentTemplate
     */
    @Deprecated
    public void deepOldClientTemplate(String componentConfig, String componentTemplate, Long componentId, Long clusterId, Integer componentTypeCode) {
        if (null == clusterId || null == componentId || null == componentTypeCode || StringUtils.isBlank(componentTemplate)) {
            throw new RdosDefineException("参数不能为空");
        }
        List<ClientTemplate> clientTemplates = null;
        if (EComponentType.noControlComponents.contains(EComponentType.getByCode(componentTypeCode))) {
            clientTemplates = ComponentConfigUtils.convertXMLConfigToComponentConfig(componentConfig);
        } else {
            clientTemplates = JSONArray.parseArray(componentTemplate, ClientTemplate.class);
        }
        for (ClientTemplate clientTemplate : clientTemplates) {
            if (clientTemplate.getId() > 0L && StringUtils.isBlank(clientTemplate.getType())) {
                //兼容旧数据 前端的自定义参数标识
                clientTemplate.setType(EFrontType.CUSTOM_CONTROL.name());
            }
        }
        clientTemplates = ComponentConfigUtils.convertOldClientTemplateToTree(clientTemplates);
        if (EComponentType.SFTP.getTypeCode().equals(componentTypeCode)) {
            ComponentConfigUtils.convertOldSftpTemplate(clientTemplates);
        }
        //提取typeName
        if (StringUtils.isNotBlank(componentConfig)) {
            String typeNameValue = JSONObject.parseObject(componentConfig).getString(TYPE_NAME_KEY);
            if (StringUtils.isNotBlank(typeNameValue)) {
                clientTemplates.add(ComponentConfigUtils.buildOthers(TYPE_NAME_KEY, typeNameValue));
            }
        }
        List<ComponentConfig> componentConfigs = ComponentConfigUtils.saveTreeToList(clientTemplates, clusterId, componentId, null, null, componentTypeCode);
        batchSaveComponentConfig(componentConfigs);
    }

    public ComponentConfig getComponentConfigByKey(Long componentId,String key) {
        return componentConfigDao.listByKey(componentId,key);
    }

    public Map<String, Object> convertComponentConfigToMap(Long componentId, boolean isFilter) {
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(componentId, isFilter);
        return ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
    }

    /**
     * 加载typeName默认的控件
     *
     * @param typeName
     * @return
     */
    public List<ComponentConfig> loadDefaultTemplate(String typeName) {
        ScheduleDict typeNameMapping = scheduleDictDao.getByNameValue(DictType.TYPENAME_MAPPING.type, typeName.trim(), null,null);
        if (null == typeNameMapping) {
            throw new RdosDefineException("不支持的插件类型");
        }
        return componentConfigDao.listByComponentId(Long.parseLong(typeNameMapping.getDictValue()), true);
    }

    public List<ComponentVO> getComponentVoByComponent(List<Component> components, boolean isFilter, Long clusterId, boolean isConvertHadoopVersion) {
        if (null == clusterId) {
            throw new RdosDefineException("集群id不能为空");
        }
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        List<ComponentConfig> componentConfigs = componentConfigDao.listByClusterId(clusterId, isFilter);
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return new ArrayList<>(0);
        }
        Map<Long, List<ComponentConfig>> componentIdConfigs = componentConfigs.stream().collect(Collectors.groupingBy(ComponentConfig::getComponentId));
        List<ComponentVO> componentVOS = new ArrayList<>(components.size());
        for (Component component : components) {
            ComponentVO componentVO = ComponentVO.toVO(component);
            List<ComponentConfig> configs = componentIdConfigs.get(component.getId());
            componentVO.setComponentTemplate(JSONObject.toJSONString(ComponentConfigUtils.buildDBDataToClientTemplate(configs)));
            componentVO.setComponentConfig(JSONObject.toJSONString(ComponentConfigUtils.convertComponentConfigToMap(configs)));
            if (isConvertHadoopVersion && EComponentType.hadoopVersionComponents.contains(EComponentType.getByCode(component.getComponentTypeCode()))) {
                //设置hadoopVersion 的key 如cdh 5.1.x
                ComponentConfig componentConfig = componentConfigDao.listByKey(component.getId(), ConfigConstant.HADOOP_VERSION);
                if (null != componentConfig) {
                    componentVO.setHadoopVersion(componentConfig.getValue());
                } else if (StringUtils.isNotBlank(component.getHadoopVersion())) {
                    //兼容老数据
                    String dependName = "hadoop3".equalsIgnoreCase(component.getHadoopVersion()) || component.getHadoopVersion().startsWith("3") ? "Hadoop3" : "Hadoop2";
                    List<ScheduleDict> hadoopVersion = scheduleDictDao.getByDependName(DictType.HADOOP_VERSION.type, dependName);
                    if (!CollectionUtils.isEmpty(hadoopVersion)) {
                        componentVO.setHadoopVersion(hadoopVersion.get(0).getDictName());
                    }
                }
            }
            componentVOS.add(componentVO);
        }
        return componentVOS;
    }
}
