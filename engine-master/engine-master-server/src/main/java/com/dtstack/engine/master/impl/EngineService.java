package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.EngineTenant;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.vo.EngineVO;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.ComponentTestResult;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.utils.EngineUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineService.class);

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private TenantDao tenantDao;

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
    public void updateResource(Long engineId, ComponentTestResult.ClusterResourceDescription description){
        Engine engine = engineDao.getOne(engineId);
        engine.setTotalCore(description.getTotalCores());
        engine.setTotalMemory(description.getTotalMemory());
        engine.setTotalNode(description.getTotalNode());

        engineDao.update(engine);
    }

    @Forbidden
    public void addEnginesByComponentConfig(JSONObject componentConfig, Long clusterId){
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
                componentService.addComponentWithConfig(engine.getId(), confName, componentConfig.getJSONObject(confName));
            }
        }
    }

    public Engine getOne(Long engineId) {
        return engineDao.getOne(engineId);
    }

    @Forbidden
    public List<EngineVO> listClusterEngines(Long clusterId, boolean queryQueue) {
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        List<EngineVO> result = EngineVO.toVOs(engines);

        if (queryQueue) {
            for (EngineVO engineVO : result) {
                List<Queue> queues = queueDao.listByEngineIdWithLeaf(engineVO.getId());
                engineVO.setQueues(QueueVO.toVOs(queues));
            }
        }

        return result;
    }
}

