package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.sftp.SftpPath;
import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.dto.ClusterDTO;
import com.dtstack.engine.dto.EngineDTO;
import com.dtstack.engine.master.component.ComponentFactory;
import com.dtstack.engine.master.component.YARNComponent;
import com.dtstack.engine.master.utils.EngineUtil;
import com.dtstack.engine.vo.ComponentVO;
import com.dtstack.engine.vo.EngineVO;
import com.dtstack.engine.vo.QueueVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineService.class);

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private TenantDao tenantDao;

    private static final String CONF_HDFS_PATH = "confHdfsPath";

    public List<QueueVO> getQueue(@Param("engineId") Long engineId){
        List<Queue> queueList = queueDao.listByEngineId(engineId);
        return QueueVO.toVOs(queueList);
    }

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }
     * ]
     */
    public String listSupportEngine(@Param("tenantId") Long dtUicTenantId){
        JSONArray array = new JSONArray();

        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null){
            return array.toJSONString();
        }

        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        List<Engine> engineList = engineDao.listByEngineIds(engineIds);
        if(CollectionUtils.isEmpty(engineList)){
            return array.toJSONString();
        }

        for (Engine engine : engineList) {
            JSONObject item = new JSONObject();
            array.add(item);
            item.put("engineType",engine.getEngineType());

            List<Component> componentList = componentService.listComponent(engine.getId());
            if (CollectionUtils.isEmpty(componentList)){
                continue;
            }

            List<Integer> componentTypes = componentList.stream()
                    .map(Component::getComponentTypeCode)
                    .collect(Collectors.toList());
            item.put("supportComponent", componentTypes);
        }

        return array.toJSONString();
    }

    /**
     * 添加多个engine
     * @param clusterDTO
     */
    public void addEngines(ClusterDTO clusterDTO){
        EngineAssert.assertTrue(Objects.nonNull(clusterDTO), ErrorCode.INVALID_PARAMETERS);
        EngineAssert.assertTrue(CollectionUtils.isNotEmpty(clusterDTO.getEngineList()), ErrorCode.INVALID_PARAMETERS);

        for (EngineDTO engineDTO : clusterDTO.getEngineList()) {
            if (Objects.nonNull(clusterDTO.getClusterId())) {
                //前端调用
                addEngine(clusterDTO.getClusterId(), engineDTO.getEngineName(), engineDTO.getComponentTypeCodeList());
            } else {
                addEngine(clusterDTO.getId(), engineDTO.getEngineName(), engineDTO.getComponentTypeCodeList());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Engine addEngine(@Param("clusterId") Long clusterId, @Param("engineName") String engineName,
                            @Param("componentTypeCodeList") List<Integer> componentTypeCodeList){
        MultiEngineType engineType = MultiEngineType.getByName(engineName);

        EngineUtil.checkComponent(engineType, componentTypeCodeList);
        checkEngineRepeat(clusterId, engineType);

        Engine engine = new Engine();
        engine.setClusterId(clusterId);
        engine.setEngineName(engineType.getName());
        engine.setEngineType(engineType.getType());
        engine.setTotalCore(0);
        engine.setTotalMemory(0);
        engine.setTotalNode(0);
        engineDao.insert(engine);

        componentService.addComponent(engine.getId(), componentTypeCodeList);

        updateEngineTenant(clusterId, engine.getId());

        return engine;
    }

    private void updateEngineTenant(Long clusterId, Long engineId){
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if(CollectionUtils.isEmpty(engines)){
            return;
        }

        List<Long> engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        engineIds.remove(engineId);
        if(CollectionUtils.isEmpty(engineIds)){
            return;
        }

        List<EngineTenant> engineTenants = engineTenantDao.listByEngineIds(engineIds);
        if(CollectionUtils.isEmpty(engineTenants)){
            return;
        }

        List<Long> tenantIds = engineTenants.stream().map(EngineTenant::getTenantId).distinct().collect(Collectors.toList());
        for (Long tenantId : tenantIds) {
            EngineTenant et = new EngineTenant();
            et.setTenantId(tenantId);
            et.setEngineId(engineId);
            engineTenantDao.insert(et);
        }
    }

    private void checkEngineRepeat(Long clusterId, MultiEngineType engineType){
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if(CollectionUtils.isEmpty(engines)){
            return;
        }

        for (Engine engine : engines) {
            if(engine.getEngineType() == engineType.getType()){
                throw new RdosDefineException("引擎类型:" + engine.getEngineName() + " 已存在，不能重复添加");
            }
        }
    }

    @Forbidden
    public List<EngineVO> listClusterEngines(Long clusterId, boolean queryQueue, boolean queryComponent, boolean withKerberosConfig){
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        List<EngineVO> result = EngineVO.toVOs(engines);

        if(queryQueue){
            queryQueue(result);
        }

        if(queryComponent){
            queryComponent(result, withKerberosConfig);
            setSecurity(result);
        }

        return result;
    }

    private void queryQueue(List<EngineVO> engineVOS){
        for (EngineVO engineVO : engineVOS) {
            List<Queue> queues = queueDao.listByEngineIdWithLeaf(engineVO.getId());
            engineVO.setQueues(QueueVO.toVOs(queues));
        }
    }

    private void queryComponent(List<EngineVO> engineVOS, boolean withKerberos){
        for (EngineVO engineVO : engineVOS) {
            List<Component> componentList = componentService.listComponent(engineVO.getEngineId());
            Map<Integer, Component> componentMap = new HashMap<>();
            componentList.stream().forEach(component -> componentMap.put(component.getComponentTypeCode(), component));
            engineVO.setComponents(ComponentVO.toVOS(componentList, withKerberos));
        }
    }

    /**
     * spark组件中添加默认地址conf_hdfs_path
     *
     * @param componentMap
     */
    private void setConfHdfsPath(Map<Integer, Component> componentMap, String clusterName) {
        Component sparkComponent = componentMap.get(EComponentType.SPARK.getTypeCode());
        Component sftpComponent = componentMap.get(EComponentType.SFTP.getTypeCode());
        if (sparkComponent != null) {
            if (sftpComponent == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIN_SFTP.getDescription());
            }
            JSONObject sftpConfig = JSONObject.parseObject(sftpComponent.getComponentConfig());
            String path = sftpConfig.getString("path");
            if (StringUtils.isBlank(path)) {
                throw new RdosDefineException(ErrorCode.SFTP_PATH_CAN_NOT_BE_EMPTY.getDescription());
            }
            JSONObject jsonObject = JSONObject.parseObject(sparkComponent.getComponentConfig());
            jsonObject.put(CONF_HDFS_PATH, String.format(ComponentService.SFTP_HADOOP_CONFIG_PATH, path ,SftpPath.CONSOLE_HADOOP_CONFIG, clusterName));
            sparkComponent.setComponentConfig(jsonObject.toString());
        }
    }

    private void setSecurity(List<EngineVO> result){
        for (EngineVO engineVO : result) {
            engineVO.setSecurity(getSecurity(engineVO));
        }
    }

    private boolean getSecurity(EngineVO engineVO){
        for (ComponentVO component : engineVO.getComponents()) {
            if(component.getComponentTypeCode() == EComponentType.HDFS.getTypeCode()){
                return component.getConfig().getBooleanValue("hadoop.security.authorization");
            }
        }

        return false;
    }

    @Forbidden
    public void updateResource(Long engineId, Map<String, Object> yarnConfig, boolean updateQueue){
        try {
            Engine engine = engineDao.getOne(engineId);
            Map<String, Object> kerberosConfig = componentService.fillKerberosConfig(JSONObject.toJSONString(yarnConfig), engine.getClusterId());
            YARNComponent yarnComponent = (YARNComponent) ComponentFactory.getComponent(kerberosConfig, EComponentType.YARN);
            yarnComponent.initClusterResource(true);
            ClusterResourceDescription description = yarnComponent.getResourceDescription();

            if(updateQueue){
                queueService.updateQueue(engineId, description);
            }

            updateResource(engineId, description);
        } catch (Exception e){
            LOGGER.error("更新引擎资源异常: ", e);
        }
    }

    @Forbidden
    public void updateResource(Long engineId, ClusterResourceDescription description){
        Engine engine = engineDao.getOne(engineId);
        engine.setTotalCore(description.getTotalCores());
        engine.setTotalMemory(description.getTotalMemory());
        engine.setTotalNode(description.getTotalNode());

        engineDao.update(engine);
    }

    /**
     * 修改引擎数据同步类型
     *
     * @param clusterId
     * @param engineType
     * @param syncType
     */
    @Forbidden
    public void updateSyncTypeByClusterIdAndEngineType(Long clusterId, Integer engineType, Integer syncType) {
        engineDao.updateSyncTypeByClusterIdAndEngineType(clusterId, engineType, syncType);
    }


    @Forbidden
    public void addEnginesByComponentConfig(JSONObject componentConfig, Long clusterId, boolean updateQueue){
        Map<Integer, List<String>> engineComponentMap = EngineUtil.classifyComponent(componentConfig.keySet());
        for (Integer integer : engineComponentMap.keySet()) {
            MultiEngineType engineType = EngineUtil.getByType(integer);

            Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, engineType.getType());
            if(engine == null){
                engine = new Engine();
                engine.setClusterId(clusterId);
                engine.setEngineName(engineType.getName());
                engine.setEngineType(engineType.getType());
                engine.setTotalCore(0);
                engine.setTotalMemory(0);
                engine.setTotalNode(0);
                engineDao.insert(engine);
            }

            for (String confName : engineComponentMap.get(integer)) {
                componentService.addComponentWithConfig(engine.getId(), confName, componentConfig.getJSONObject(confName), updateQueue);
            }
        }
    }

    public Engine getOne(Long engineId) {
        Engine one = engineDao.getOne(engineId);
        return one;
    }
}

